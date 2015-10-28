package com.linkedin.parseq.blocking;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.internal.ArgumentUtil;


/**
 * {@code BlockingEngine} is a wrapper around an {@code Engine} that allows throttling execution of tasks by
 * blocking a thread that starts a new plan until Engine has a capacity to execute it.
 * This type of throttling mechanism is useful in situations where synchronous,
 * single-threaded task producer needs a blocking throttling e.g. in kafka consumer.
 * If there are multiple single-threaded task producers, e.g. kafka consumers of different topics,
 * they should all share one instance of {@code BlockingEngine}.
 * <p>
 * Example usage in kafka consumer:
 * <blockquote><pre>
 * while (_iterator.hasNext()) {
 *   // get next kafka message
 *   Record record = _iterator.next();
 *
 *   // create task that will process record
 *   Task{@code <?>} task = processRecord(record);
 *
 *   // will block until engine has capacity to process the task
 *   _blockingEngine.run(task);
 * }
 * </pre></blockquote>
 * {@code BlockingEngine} can be parameterized by specifying how many concurrent plans can be running at any given time.
 * If tasks are doing only synchronous operations then good value for the max concurrency is number of cores.
 * If tasks are involved in asynchronous operations such as IO then in order to maximize CPU utilization
 * max concurrent plans should be higher than number of available cores because it is expected that some of the plans
 * will be suspended waiting for IO operations to complete.
 * <p>
 * {@code BlockingEngine} also provides a FIFO queue in which tasks wait until Engine has capacity to execute them.
 * <p>
 * This class is not intended to be used in reactive settings because it is blocking.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BlockingEngine {

  private final Engine _engine;
  private final Semaphore _demand;
  private final Queue<Task<?>> _queue = new ConcurrentLinkedQueue<>();
  private final AtomicInteger _pending = new AtomicInteger(0);
  private final int _maxConcurrentPlans;

  /**
   * Creates a wrapper around an Engine that allows throttling execution of tasks by
   * blocking a thread that starts a new plan until Engine has a capacity to execute it.
   * <p>
   * {@code maxConcurrentPlans} specifies how many concurrent plans can be running at any given time.
   * If task can not be immediately executed because there are {@code maxConcurrentPlans}
   * plans currently running the task will be added to the waiting FIFO queue.
   *
   * @param engine Engine that will be used to execute tasks
   * @param maxConcurrentPlans maximum number of plans that can be concurrently executed by the Engine
   * @param queueSize size of the queue for tasks waiting to be executed
   */
  public BlockingEngine(Engine engine, int maxConcurrentPlans, int queueSize) {
    ArgumentUtil.requirePositive(maxConcurrentPlans, "maxConcurrentPlans");
    ArgumentUtil.requirePositive(queueSize, "queueSize");
    _maxConcurrentPlans = maxConcurrentPlans;
    _demand = new Semaphore(maxConcurrentPlans + queueSize);
    _engine = engine;
  }

  /**
   * Runs the given task blocking and waiting until Engine has a capacity to start new plan
   * or there is a space in the queue to put this task on.
   * All tasks created and started as a consequence
   * of this task will belong to that plan and will share a Trace.
   * @param task
   * @throws InterruptedException
   */
  public void run(final Task<?> task) throws InterruptedException {
    _demand.acquire();
    runWithPermit(task);
  }

  private void runWithPermit(final Task<?> task) {

    task.addListener(p -> {
      _demand.release();
      if (_pending.getAndDecrement() > _maxConcurrentPlans) {
        _engine.run(_queue.poll());
      }
    });

    _queue.offer(task);
    if (_pending.getAndIncrement() < _maxConcurrentPlans) {
      _engine.run(_queue.poll());
    }
  }

  /**
   * Runs the given task if Engine has a capacity to start new plan or there is a space
   * in the queue to put this task on.
   * All tasks created and started as a consequence
   * of this task will belong to that plan and will share a Trace.
   * @param task
   * @return true if Plan was started or placed in the queue
   */
  public boolean tryRun(final Task<?> task) {
    if (_demand.tryAcquire()) {
      runWithPermit(task);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Runs the given task blocking and waiting up to specified amount of time until Engine
   * has a capacity to start new plan or there is a space in the queue to put this task on.
   * All tasks created and started as a consequence
   * of this task will belong to that plan and will share a Trace.
   * @param task
   * @return true if Plan was started or placed in the queue within the given waiting time and the current thread has not
   * been {@linkplain Thread#interrupt interrupted}.
   * @throws InterruptedException
  */
  public boolean tryRun(final Task<?> task, final long timeout, final TimeUnit unit) throws InterruptedException {
    if (_demand.tryAcquire(timeout, unit)) {
      runWithPermit(task);
      return true;
    } else {
      return false;
    }
  }
}
