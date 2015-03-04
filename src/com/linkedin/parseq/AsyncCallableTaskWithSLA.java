package com.linkedin.parseq;


import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import java.lang.IllegalStateException;import java.lang.Override;import java.lang.Runnable;import java.lang.String;import java.lang.Throwable;import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


/**
 * Calls a synchronous task in a separate thread to make it asynchronous.  Supports SLA via a timeout in Milliseconds.  Returns a default value on timeout or error.
 * To use this class three methods may be overridden.  The first method, <code>doTask</code> must be overridden and the second two, <code>onError</code> and <code>onTimeout</code> may be optionally overridden.
 * <ul>
 * <li>doTask</li> does the work that is expected to return a result.
 * <li>onError</li> is called if doTask throws an exception.  If it returns successfully, the promise will succeed with the result, otherwise the promise will fail.
 * <li>onTimeout</li> is called if doTask takes longer than timeoutInMillis ms to complete.  If it returns successfully, the promise will succeed with the result, otherwise the promise will fail.
 * </ul>
 * The <code>taskState</code> property indicates the state of the task (one of <code>{UNDEFINED,SUCCESS,TIMEDOUT,ERROR_ON_TIMEOUT,ERROR,ERROR_ON_ERROR}</code>).
 * If an error occurs during task processing, it saved in the <code>primaryError</code> property.  If an error occurs while
 * calling <code>onError</code> or <code>onTimeout</code>, it saved in the <code>secondaryError</code> property.
 *
 * @author Eric Melz (emelz@linkedin.com)
 */
public abstract class AsyncCallableTaskWithSLA<A> extends BaseTask<A>
{
  private final long _timeoutInMillis;
  public static final String CALLABLE_SERVICE_EXECUTOR = "_CallableServiceExecutor_";
  private Throwable _primaryError;
  private Throwable _secondaryError;
  private TaskState _taskState;

  public Throwable getPrimaryError()
  {
    return _primaryError;
  }

  public Throwable getSecondaryError()
  {
    return _secondaryError;
  }

  public TaskState getTaskState()
  {
    return _taskState;
  }

  public AsyncCallableTaskWithSLA(long timeoutInMillis)
  {
    super();
    _timeoutInMillis = timeoutInMillis;
    _taskState = TaskState.UNDEFINED;
  }

  enum TaskState {
    UNDEFINED,
    SUCCESS,
    TIMEDOUT,
    ERROR_ON_TIMEOUT,
    ERROR,
    ERROR_ON_ERROR
  }

  public static void register(EngineBuilder builder, Executor executor)
  {
    builder.setEngineProperty(CALLABLE_SERVICE_EXECUTOR, executor);
  }

  @Override
  protected Promise<? extends A> run(final Context context) throws Throwable
  {
    Executor executor = (Executor)context.getEngineProperty(CALLABLE_SERVICE_EXECUTOR);

    if (executor == null)
    {
      throw new IllegalStateException("To use this AsyncCallableTask you must first register an executor with the engine using AsyncCallableTask.register");
    }

    final SettablePromise<A> promise = Promises.settable();
    executor.execute(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          A result = doTask();
          _taskState = TaskState.SUCCESS;
          promise.done(result);
        }
        catch (Throwable t)
        {
          A onErrorResult = null;
          try
          {
            _taskState = TaskState.ERROR;
            _primaryError = t;
            onErrorResult = onError();

          } catch (Throwable t2)
          {
            _taskState = TaskState.ERROR_ON_ERROR;
            _secondaryError = t2;
          }
          if (onErrorResult == null)
          {
            if (!promise.isDone())
            {
              promise.fail(null);
            }
          }
          else
          {
            if (!promise.isDone())
            {
              promise.done(onErrorResult);
            }
          }
        }
      }
    });

    context.createTimer(_timeoutInMillis, TimeUnit.MILLISECONDS, Tasks.action("timeout", new Runnable()
    {
      @Override
      public void run()
      {
        A onTimeoutResult = null;
        try
        {
          _taskState = TaskState.TIMEDOUT;
          onTimeoutResult = onTimeout();

        }
        catch (Throwable e)
        {
          _secondaryError = e;
          _taskState = TaskState.ERROR_ON_TIMEOUT;
        }
        if (onTimeoutResult == null)
        {
          promise.fail(null);
        }
        else
        {
          promise.done(onTimeout());
        }
      }
    }));

    return promise;
  }

  /**
   * Do the actual work of the task.  This will be called in a separate thread.
   * @return
   */
  abstract protected A doTask();

  /**
   * Do some action if an error occurs during doTask.  If a non-null value is returned, the promise is marked done
   * with the returned value.  If a null value is returned, or an exception occurs, the promise is failed.
   * @return default value when an error occurs
   */
  protected A onError()
  {
    return null;
  }

  /**
   * Do some action if a timeout occurs during doTask.  If a non-null value is returned, the promise is marked done
   * with the returned value.  If a null value is returned, or an exception occurs, the promise is failed.
   * @return default value when an error occurs
   */
  protected A onTimeout()
  {
    return null;
  }
}
