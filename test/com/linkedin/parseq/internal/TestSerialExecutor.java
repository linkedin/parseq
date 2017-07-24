package com.linkedin.parseq.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.linkedin.parseq.internal.SerialExecutor.DeactivationListener;

public class TestSerialExecutor {
  private ExecutorService _executorService;
  private CapturingExceptionHandler _rejectionHandler;
  private SerialExecutor _serialExecutor;
  private CapturingActivityListener _capturingDeactivationListener;

  @DataProvider(name = "draining")
  public Object[][] testDataProvider() {
    return new Object[][] { { true }, { false } };
  }

  @BeforeMethod
  public void setUp(Object[] testArgs) {
    _executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
        new ThreadPoolExecutor.AbortPolicy());
    _rejectionHandler = new CapturingExceptionHandler();
    _capturingDeactivationListener = new CapturingActivityListener();
    _serialExecutor = new SerialExecutor(_executorService, _rejectionHandler, _capturingDeactivationListener,
        new FIFOPriorityQueue<>(), (boolean)testArgs[0], null);
  }

  @AfterMethod
  public void tearDown() throws InterruptedException {
    _serialExecutor = null;
    _rejectionHandler = null;
    _executorService.shutdownNow();
    _executorService.awaitTermination(1, TimeUnit.SECONDS);
    _executorService = null;
  }

  @Test(dataProvider = "draining")
  public void testExecuteOneStepPlan(boolean draining) throws InterruptedException {
    final LatchedRunnable runnable = new LatchedRunnable();
    _serialExecutor.execute(runnable);
    assertTrue(runnable.await(5, TimeUnit.SECONDS));
    assertFalse(_rejectionHandler.wasExecuted());
    assertTrue(_capturingDeactivationListener.await(5, TimeUnit.SECONDS));
    assertEquals(_capturingDeactivationListener.getDeactivatedCount(), 1);
  }

  @Test(dataProvider = "draining")
  public void testExecuteTwoStepPlan(boolean draining) throws InterruptedException {
    final LatchedRunnable inner = new LatchedRunnable();

    final Runnable outer = new Runnable() {
      @Override
      public void run() {
        _serialExecutor.execute(inner);
      }
    };

    _executorService.execute(outer);
    assertTrue(inner.await(5, TimeUnit.SECONDS));
    assertFalse(_rejectionHandler.wasExecuted());
    assertTrue(_capturingDeactivationListener.await(5, TimeUnit.SECONDS));
    assertEquals(_capturingDeactivationListener.getDeactivatedCount(), 1);
  }

  @Test(dataProvider = "draining")
  public void testRejectOnFirstExecute(boolean draining) throws InterruptedException {
    // First fill up the underlying executor service so that a subsequent
    // submission of an execution loop by the serial executor will fail.
    _executorService.execute(new NeverEndingRunnable());
    assertFalse(_rejectionHandler.wasExecuted());
    _executorService.execute(new NeverEndingRunnable());
    assertFalse(_rejectionHandler.wasExecuted());

    // Now submit our task to serial executor. The underlying executor should
    // throw RejectedExecutionException and the rejectionRunnable should run.
    _serialExecutor.execute(new NeverEndingRunnable());
    assertTrue(_rejectionHandler.await(5, TimeUnit.SECONDS));
    assertTrue(
        "Expected " + _rejectionHandler.getLastError() + " to be instance of "
            + RejectedExecutionException.class.getName(),
        _rejectionHandler.getLastError() instanceof RejectedExecutionException);
  }

  @Test(dataProvider = "draining")
  public void testRejectOnLoop(boolean draining) throws InterruptedException {
    final CountDownLatch innerLatch = new CountDownLatch(1);
    final CountDownLatch outerLatch = new CountDownLatch(1);
    final LatchedRunnable inner = new LatchedRunnable();
    final LatchedRunnable outer = new LatchedRunnable() {
      @Override
      public void run() {
        try {
          outerLatch.countDown();
          innerLatch.await();
          _serialExecutor.execute(inner);
        } catch (InterruptedException e) {
          // Shouldn't happen
        }
        super.run();
      }
    };

    // First we submit the outer task. This task will wait until for a count
    // down on the inner latch.
    _serialExecutor.execute(outer);
    outerLatch.await();

    // Now we submit another runnable to the underlying executor. This should
    // get queued up.
    _executorService.execute(new NeverEndingRunnable());

    // Now the inner task will be submitted. This will eventually result in
    // the re-execution of the serial executor loop, but that re-submission will
    // fail because the thread is tied up by the current loop and the queue is
    // saturated with the previously submitted runnable.
    innerLatch.countDown();

    if (!draining) {
      assertTrue(_rejectionHandler.await(5, TimeUnit.SECONDS));
      assertTrue(
          "Expected " + _rejectionHandler.getLastError() + " to be instance of "
              + RejectedExecutionException.class.getName(),
          _rejectionHandler.getLastError() instanceof RejectedExecutionException);
    } else {
      assertFalse(_rejectionHandler.wasExecuted());
    }
  }

  @Test(dataProvider = "draining")
  public void testThrowingRunnable(boolean draining) throws InterruptedException {
    _serialExecutor.execute(new ThrowingRunnable());
    assertTrue(_rejectionHandler.await(5, TimeUnit.SECONDS));
    assertTrue(
        "Expected " + _rejectionHandler.getLastError() + " to be instance of "
            + RuntimeException.class.getName(),
        _rejectionHandler.getLastError() instanceof RuntimeException);
  }

  private static class NeverEndingRunnable implements PrioritizableRunnable {
    @Override
    public void run() {
      try {
        new CountDownLatch(1).await();
      } catch (InterruptedException e) {
        // This is our shutdown mechanism.
      }
    }
  }

  private static class ThrowingRunnable implements PrioritizableRunnable {
    @Override
    public void run() {
      throw new RuntimeException();
    }
  }

  private static class CapturingActivityListener implements DeactivationListener {

    private AtomicInteger _deactivatedCount = new AtomicInteger();
    private final CountDownLatch _latch = new CountDownLatch(1);

    @Override
    public void deactivated() {
      _deactivatedCount.incrementAndGet();
      _latch.countDown();
    }

    public int getDeactivatedCount() {
      return _deactivatedCount.get();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return _latch.await(timeout, unit);
    }
  }

  private static class CapturingExceptionHandler implements UncaughtExceptionHandler {
    private final CountDownLatch _latch = new CountDownLatch(1);
    private volatile Throwable _lastError;

    @Override
    public void uncaughtException(Throwable error) {
      _lastError = error;
      _latch.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return _latch.await(timeout, unit);
    }

    public boolean wasExecuted() {
      return _latch.getCount() == 0;
    }

    public Throwable getLastError() {
      return _lastError;
    }
  }

  private static class LatchedRunnable implements PrioritizableRunnable {
    private final CountDownLatch _latch = new CountDownLatch(1);

    @Override
    public void run() {
      _latch.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return _latch.await(timeout, unit);
    }
  }
}
