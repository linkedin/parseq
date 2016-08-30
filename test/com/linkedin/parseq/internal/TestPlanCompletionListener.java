package com.linkedin.parseq.internal;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.linkedin.parseq.Task.value;


/**
 * @author Ang Xu
 */
public class TestPlanCompletionListener extends BaseEngineTest  {

  private TraceCaptureListener _traceCaptureListener;

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    _traceCaptureListener = new TraceCaptureListener();
    engineBuilder.setPlanCompletionListener(_traceCaptureListener);
  }

  @Test
  public void testSingleTask() {
    Task<?> task = value("taskName", "value").andThen(value("value2"));

    runAndWait(task);

    Assert.assertTrue(_traceCaptureListener.isDone());
  }

  @Test
  public void testUnfinishedTask() {
    Task<?> task = value("unFinished", "value");

    Assert.assertFalse(_traceCaptureListener.isDone());
  }

  @Test
  public void testWithTimerTask() {
    Task<?> task = delayedValue("value", 1000, TimeUnit.MILLISECONDS).withTimeout(50, TimeUnit.MILLISECONDS);

    runAndWaitException(task, TimeoutException.class);

    Assert.assertTrue(_traceCaptureListener.isDone());

    Trace trace = _traceCaptureListener.getTrace();
    Assert.assertNotNull(trace);

    Optional<ShallowTrace> timerTrace = findTraceByName(trace, "withTimeout");
    Assert.assertTrue(timerTrace.isPresent());
    Assert.assertEquals(timerTrace.get().getResultType(), ResultType.ERROR);

    Optional<ShallowTrace> valueTrace = findTraceByName(trace, "value");
    Assert.assertTrue(valueTrace.isPresent());
    Assert.assertEquals(valueTrace.get().getResultType(), ResultType.EARLY_FINISH);
  }

  @Test
  public void testWithSideEffect() throws InterruptedException {
    Task<?> task = value("value1Task", "value1").withSideEffect("delayed sideEffect",
        v -> delayedValue("value2", 100, TimeUnit.MILLISECONDS));

    runAndWait(task);

    Assert.assertTrue(_traceCaptureListener.await(30, TimeUnit.SECONDS));

    Trace trace = _traceCaptureListener.getTrace();
    Assert.assertNotNull(trace);

    Optional<ShallowTrace> sideEffectTrace = findTraceByName(trace, "delayed sideEffect");
    Assert.assertTrue(sideEffectTrace.isPresent());
    Assert.assertEquals(sideEffectTrace.get().getResultType(), ResultType.SUCCESS);

    Optional<ShallowTrace> valueTrace = findTraceByName(trace, "value2 delayed");
    Assert.assertTrue(valueTrace.isPresent());
    Assert.assertEquals(valueTrace.get().getResultType(), ResultType.SUCCESS);
  }

  @Test
  public void testWithSideEffect2() throws InterruptedException {
    Task<?> task = value("value1Task", "value1").withSideEffect("delayed sideEffect",
        v -> delayedValue("value2", 100, TimeUnit.MILLISECONDS));

    runAndWaitForPlanToComplete("side-effect task", task, 30, TimeUnit.SECONDS);
    Trace trace = task.getTrace();
    Assert.assertNotNull(trace);

    Optional<ShallowTrace> sideEffectTrace = findTraceByName(trace, "delayed sideEffect");
    Assert.assertTrue(sideEffectTrace.isPresent());
    Assert.assertEquals(sideEffectTrace.get().getResultType(), ResultType.SUCCESS);

    Optional<ShallowTrace> valueTrace = findTraceByName(trace, "value2 delayed");
    Assert.assertTrue(valueTrace.isPresent());
    Assert.assertEquals(valueTrace.get().getResultType(), ResultType.SUCCESS);
  }

  private Optional<ShallowTrace> findTraceByName(Trace trace, String name) {
    return trace.getTraceMap().values().stream()
        .filter(shallowTrace -> shallowTrace.getName().contains(name))
        .findAny();
  }

  private class TraceCaptureListener implements PlanCompletionListener {

    private volatile CountDownLatch _countDownLatch = new CountDownLatch(1);
    private final AtomicReference<Trace> _traceRef = new AtomicReference<>();

    @Override
    public void onPlanCompleted(PlanContext planContext) {
      _traceRef.set(planContext.getRelationshipsBuilder().build());
      _countDownLatch.countDown();
    }

    public Trace getTrace() {
      return _traceRef.get();
    }

    public boolean isDone() {
      return _countDownLatch.getCount() == 0;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return _countDownLatch.await(timeout, unit);
    }
  }

}
