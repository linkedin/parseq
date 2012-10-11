package com.linkedin.parseq;

import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Chris Pettitt
 */
/* package private */ class SeqActionsTask extends BaseTask<Void>
{
  private final List<Task<Object>> _tasks;

  public SeqActionsTask(final String name, final Iterable<? extends Task<?>> tasks)
  {
    super(name);
    List<Task<Object>> taskList = new ArrayList<Task<Object>>();
    for(Task<?> task : tasks)
    {
      taskList.add(InternalUtil.unwildcardTask(task));
    }

    if (taskList.size() == 0)
    {
      throw new IllegalArgumentException("No tasks to sequence!");
    }

    _tasks = Collections.unmodifiableList(taskList);
  }

  @Override
  protected Promise<Void> run(final Context context) throws Exception
  {
    final SettablePromise<Void> result = Promises.settable();
    final AtomicInteger count = new AtomicInteger(_tasks.size());
    final PromiseListener<Object> listener = new PromiseListener<Object>()
    {
      @Override
      public void onResolved(final Promise<Object> promise)
      {
        if (promise.isFailed())
        {
          if (count.get() > 0) {
            count.set(0);
            result.fail(promise.getError());
          }
        }
        else
        {
          if (count.decrementAndGet() == 0)
          {
            result.done(null);
          }
        }
      }
    };

    Task<Object> prevTask = _tasks.get(0);
    prevTask.addListener(listener);
    for (int i = 1; i < _tasks.size(); i++)
    {
      final Task<Object> currTask = _tasks.get(i);
      currTask.addListener(listener);
      context.after(prevTask).run(currTask);
      prevTask = currTask;
    }

    context.run(_tasks.get(0));
    return result;
  }

  @Override
  public ShallowTrace getShallowTrace()
  {
    ShallowTrace shallowTrace = super.getShallowTrace();
    ShallowTraceBuilder builder = new ShallowTraceBuilder(shallowTrace);
    builder.setSystemHidden(true);
    return builder.build();
  }
}
