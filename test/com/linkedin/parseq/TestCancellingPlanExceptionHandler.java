package com.linkedin.parseq;


import com.linkedin.parseq.internal.PrioritizableRunnable;
import com.linkedin.parseq.internal.SerialExecutor;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.Test;


public class TestCancellingPlanExceptionHandler extends BaseEngineTest {

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setTaskQueueFactory(ThrowingTaskQueue::new);
  }

  @Test
  public void testThrowingTaskQueue() {
    Task<Integer> task = Task.value(1);

    runAndWaitException(task, CancellationException.class, 5, TimeUnit.SECONDS);
  }

  private static class ThrowingTaskQueue implements SerialExecutor.TaskQueue<PrioritizableRunnable> {

    @Override
    public void add(PrioritizableRunnable value) {
    }

    @Override
    public PrioritizableRunnable poll() {
      return () -> { throw new RuntimeException(); };
    }
  }
}
