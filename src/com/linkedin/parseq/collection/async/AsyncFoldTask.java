package com.linkedin.parseq.collection.async;

import java.util.Optional;
import java.util.function.Consumer;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.FusionTask;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.TaskOrValue;
import com.linkedin.parseq.collection.transducer.Reducer;
import com.linkedin.parseq.collection.transducer.Reducer.Step;
import com.linkedin.parseq.collection.transducer.Ref;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class AsyncFoldTask<Z, T> extends BaseTask<Z> implements Ref<Z> {

  private Publisher<TaskOrValue<T>> _tasks;
  private boolean _streamingComplete = false;
  private int _pending = 0;
  private Z _partialResult;
  private Subscription _subscription;
  private final Reducer<Z, T> _reducer;
  private final Optional<Task<?>> _predecessor;

  public AsyncFoldTask(final String name, final Publisher<TaskOrValue<T>> tasks, final Z zero,
      final Reducer<Z, T> reducer, Optional<Task<?>> predecessor) {
    super(name);
    _partialResult = zero;
    _reducer = reducer;
    _tasks = tasks;
    _predecessor = predecessor;
  }

  //TODO: when result is resolved, then tasks should be early finished, not started?

  @Override
  protected Promise<? extends Z> run(final Context context) throws Exception
  {
    final SettablePromise<Z> result = Promises.settable();

    _tasks.subscribe(new Subscriber<TaskOrValue<T>>() {

      private void onNextStep(Step<Z> step) {
        switch (step.getType()) {
          case cont:
            _partialResult = step.getValue();
            if (_streamingComplete && _pending == 0) {
              result.done(_partialResult);
              _partialResult = null;
            }
            break;
          case done:
            _partialResult = null;
            _subscription.cancel();
            result.done(step.getValue());
            break;
        }
      }

      private void onNextValue(TaskOrValue<T> tValue) {
        try {
          TaskOrValue<Step<Z>> step = _reducer.apply(AsyncFoldTask.this, tValue);
          if (step.isTask()) {
            _pending++;
            //TODO introduce fusable task type and use it here,
            //this is temporary hack
            step.getTask().addListener(p -> {
              if (p.isFailed()) {
                _subscription.cancel();
                _partialResult = null;
                if (!result.isDone()) {
                  result.fail(p.getError());
                }
              } else {
                _pending--;
                onNextStep(p.get());
              }
            });
          scheduleTask(step.getTask(), context, AsyncFoldTask.this);
//            scheduleTask(fusedPropgatingTask("reduce", step.getTask(),
//                s -> {
//                  _pending--;
//                  onNextStep(s);
//                }), context, AsyncFoldTask.this);
          } else {
            onNextStep(step.getValue());
          }
        } catch (Throwable e) {
          _streamingComplete = true;
          _partialResult = null;
          result.fail(e);
        }
      }

      private void onNextTask(Task<T> task) {
        _pending++;
        scheduleTask(fusedPropgatingTask("step", task,
            t -> {
              _pending--;
              onNextValue(TaskOrValue.value(t));
            }), context, AsyncFoldTask.this);
      }

      private <A> FusionTask<?, A> fusedPropgatingTask(final String description, final Task<A> task, final Consumer<A> consumer) {
        return FusionTask.fuse(description, task,
            (p, t) -> {
              try
              {
                //propagate result
                if (p.isFailed()) {
                  t.fail(p.getError());
                } else {
                  t.done(p.get());
                }
              } finally {
                if (!result.isDone()) {
                  if (p.isFailed()) {
                    _subscription.cancel();
                    _partialResult = null;
                    result.fail(p.getError());
                  } else {
                    consumer.accept(p.get());
                  }
                } else {
                  /**
                   * result is resolved, it means that stream has completed or
                   * it has been cancelled
                   */
                }
              }
            }, Optional.empty());
      }

      /**
       * It is expected that onNext method is called
       * from within Task's run method.
       */
      @Override
      public void onNext(final TaskOrValue<T> taskOrValue) {
        if (taskOrValue.isTask()) {
          onNextTask(taskOrValue.getTask());
        } else {
          onNextValue(taskOrValue);
        }
      }

      @Override
      public void onComplete() {
        _streamingComplete = true;
        if (_pending == 0) {
          if (!result.isDone()) {
            result.done(_partialResult);
            _partialResult = null;
          }
        }
      }

      @Override
      public void onError(Throwable cause) {
        _streamingComplete = true;
        if (!result.isDone()) {
          result.fail(cause);
          _partialResult = null;
        }
      }

      @Override
      public void onSubscribe(Subscription subscription) {
        _subscription = subscription;
      }
    });

    if (_predecessor.isPresent()) {
      context.run(_predecessor.get());
    }

    _tasks = null;
    return result;
  }

  void scheduleTask(Task<?> task, Context context, Task<?> rootTask) {
    context.runSubTask(task, rootTask);
  }

  @Override
  public Z refGet() {
    return _partialResult;
  }

  @Override
  public void refSet(Z t) {
    _partialResult = t;
  }

}
