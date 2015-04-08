package com.linkedin.parseq;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.function.Consumer3;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.Continuations;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromisePropagator;
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.Settable;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;
import com.linkedin.parseq.trace.TraceRelationship;


/**
 * TODO optimize flatmap - can it be run without rescheduling?
 * TODO fix flatmap trace - possibly by reimplementing flatten
 *
 *
 * @author jodzga
 *
 * @param <S>
 * @param <T>
 */
public class FusionTask<S, T> extends BaseTask<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FusionTask.class);

  private static final Continuations CONTINUATIONS = new Continuations();

  private final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> _propagator;
  private final Task<S> _task;
  private final Promise<S> _source;

  private final ShallowTraceBuilder _predecessor;

  private FusionTask(final String desc, final Task<S> task, final Promise<S> source,
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator, final boolean systemHidden,
      final ShallowTraceBuilder predecessor) {
    super(desc);
    _propagator = completing(propagator);
    _task = task;
    _source = source;
    _shallowTraceBuilder.setSystemHidden(systemHidden);
    _predecessor = predecessor;
  }

  private void trnasitionToDone(final FusionTraceContext traceContext) {
    addRelationships(traceContext);
    transitionPending();
    transitionDone();
  }

  private void addRelationships(final FusionTraceContext traceContext) {
    TraceBuilder builder = getTraceBuilder();
    if (traceContext.getParent() != null && !traceContext.getParent().getId().equals(getId())) {
      builder.addRelationship(Relationship.PARENT_OF, traceContext.getParent(), _shallowTraceBuilder);
    }
    if (_predecessor != null
        && (traceContext.getParent() == null || !traceContext.getParent().getId().equals(getId()))) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, _predecessor);
    } else if (traceContext.getSource() != null
        && (traceContext.getParent() == null || !traceContext.getParent().getId().equals(getId()))) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, traceContext.getSource());
    } else if (_predecessor != null && (traceContext.getParent() == null
        || (traceContext.getParent().getId().equals(getId()) && !builder.containsRelationship(
            new TraceRelationship(getId(), _predecessor.getId(), Relationship.PARENT_OF))))) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, _predecessor);
    }
  }

  private void addPotentialRelationships(final FusionTraceContext traceContext, final TraceBuilder builder) {
    if (traceContext.getParent() != null && !traceContext.getParent().getId().equals(getId())) {
      builder.addRelationship(Relationship.POTENTIAL_CHILD_OF, _shallowTraceBuilder, traceContext.getParent());
    }
    if (_predecessor != null
        && (traceContext.getParent() == null || !traceContext.getParent().getId().equals(getId()))) {
      builder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, _shallowTraceBuilder, _predecessor);
    } else if (traceContext.getSource() != null
        && (traceContext.getParent() == null || !traceContext.getParent().getId().equals(getId()))) {
      builder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, _shallowTraceBuilder, traceContext.getSource());
    } else if (_predecessor != null
        && (traceContext.getParent() == null || (traceContext.getParent().getId().equals(getId())
            && !builder.containsRelationship(
                new TraceRelationship(getId(), _predecessor.getId(), Relationship.PARENT_OF))
        && !builder.containsRelationship(
            new TraceRelationship(_predecessor.getId(), getId(), Relationship.POTENTIAL_CHILD_OF))))) {
      builder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, _shallowTraceBuilder, _predecessor);
    }
  }

  private Consumer3<FusionTraceContext, Promise<S>, Settable<T>> completing(
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator) {
    return (traceContext, src, dest) -> {
      final SettablePromise<T> settable = FusionTask.this.getSettableDelegate();

      if (traceContext.getParent() != null && traceContext.getParent().getId().equals(getId())) {
        //parent fusion tasks simply propagate result because the BaseTask's code
        //handles completing the task
        propagator.accept(traceContext, src, new Settable<T>() {
          @Override
          public void done(T value) throws PromiseResolvedException {
            dest.done(value);
          }

          @Override
          public void fail(Throwable error) throws PromiseResolvedException {
            dest.fail(error);
          }
        });
      } else if (transitionRun(traceContext.getTraceBuilder())) {
        //non-parent task executed for the first time
        traceContext.getTaskLogger().logTaskStart(this);
        Runnable complete = () -> {
          try {
            propagator.accept(traceContext, src, new Settable<T>() {
              @Override
              public void done(final T value) throws PromiseResolvedException {
                try {
                  trnasitionToDone(traceContext);
                  final Function<T, String> traceValueProvider = _traceValueProvider;
                  _shallowTraceBuilder.setResultType(ResultType.SUCCESS);
                  if (traceValueProvider != null) {
                    try {
                      _shallowTraceBuilder.setValue(traceValueProvider.apply(value));
                    } catch (Exception e) {
                      _shallowTraceBuilder.setValue(e.toString());
                    }
                  }
                  settable.done(value);
                  traceContext.getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
                  CONTINUATIONS.submit(() -> dest.done(value), null);
                } catch (Exception e) {
                  CONTINUATIONS.submit(() -> dest.fail(e), null);
                }
              }

              @Override
              public void fail(final Throwable error) throws PromiseResolvedException {
                try {
                  trnasitionToDone(traceContext);
                  traceFailure(error);
                  settable.fail(error);
                  traceContext.getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
                  CONTINUATIONS.submit(() -> dest.fail(error), null);
                } catch (Exception e) {
                  CONTINUATIONS.submit(() -> dest.fail(e), null);
                }
              }
            });
          } catch (Exception e) {
            LOGGER.error("An exception was thrown by propagator", e);
          }
        };
        CONTINUATIONS.submit(complete, null);
      } else {
        //non-parent tasks subsequent executions
        addPotentialRelationships(traceContext, traceContext.getTraceBuilder());
        Promises.propagateResult(settable, dest);
      }
    };
  }

  private static <A, B> Consumer3<FusionTraceContext, Promise<A>, Settable<B>> withFusionTraceContext(
      final PromisePropagator<A, B> propagator) {
    return (traceContext, src, dest) -> propagator.accept(src, dest);
  }

  @SuppressWarnings("unchecked")
  public static <S, T> FusionTask<?, T> fuse(final String name, final Task<S> task,
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator,
      final ShallowTraceBuilder predecessor) {
    if (task instanceof FusionTask) {
      return ((FusionTask<?, S>) task).apply(name, propagator);
    } else {
      return new FusionTask<S, T>(name, task, task, propagator, false, predecessor);
    }
  }

  public static <S, T> FusionTask<?, T> create(final String name, final Promise<S> source,
      final PromisePropagator<S, T> propagator) {
    return new FusionTask<S, T>(name, null, source, withFusionTraceContext(propagator), false, null);
  }

  private <R> Consumer3<FusionTraceContext, Promise<S>, Settable<R>> compose(
      final Consumer3<FusionTraceContext, Promise<T>, Settable<R>> propagator) {
    return (traceContext, src, dst) -> {
      _propagator.accept(traceContext, src, new Settable<T>() {
        @Override
        public void done(T value) throws PromiseResolvedException {
          try {
            propagator.accept(traceContext, Promises.value(value), dst);
          } catch (Exception e) {
            LOGGER.error("An exception was thrown by propagator", e);
          }
        }

        @Override
        public void fail(Throwable error) throws PromiseResolvedException {
          try {
            propagator.accept(traceContext, Promises.error(error), dst);
          } catch (Exception e) {
            LOGGER.error("An exception was thrown by propagator", e);
          }
        }
      });
    };
  }

  @Override
  public <R> Task<R> apply(String desc, PromisePropagator<T, R> propagator) {
    return apply(desc, withFusionTraceContext(propagator));
  }

  public <R> FusionTask<?, R> apply(String desc, Consumer3<FusionTraceContext, Promise<T>, Settable<R>> propagator) {
    return fuse(desc, _task, compose(propagator), _shallowTraceBuilder);
  };

  @Override
  public Task<T> recoverWith(final String desc, final Function1<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return Task.async(desc, context -> {
      final SettablePromise<T> result = Promises.settable();
      context.after(that).run(() -> {
        if (that.isFailed() && !(Exceptions.isCancellation(that.getError()))) {
          try {
            Task<T> r = func.apply(that.getError());
            Promises.propagateResult(r, result);
            return r;
          } catch (Throwable t) {
            result.fail(t);
            return null;
          }
        } else {
          result.done(that.get());
          return null;
        }
      } );
      context.run(that);
      return result;
    } , true);
  }

  protected void propagate(final FusionTraceContext traceContext, final SettablePromise<T> result) {
    try {
      _propagator.accept(traceContext, _source, result);
    } catch (Throwable t) {
      result.fail(t);
    }
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Throwable {
    final SettablePromise<T> result = Promises.settable();
    if (_task == null) {
      FusionTraceContext traceContext = new FusionTraceContext(context.getTraceBuilder(), null,
          FusionTask.this.getShallowTraceBuilder(), context.getTaskLogger());
      propagate(traceContext, result);
    } else {
      final Task<T> propagationTask = Task.async(getName(), ctx -> {
        FusionTraceContext traceContext = new FusionTraceContext(ctx.getTraceBuilder(), _task.getShallowTraceBuilder(),
            FusionTask.this.getShallowTraceBuilder(), ctx.getTaskLogger());
        if (_predecessor != null) {
          ctx.getTraceBuilder().addRelationship(Relationship.SUCCESSOR_OF, ctx.getShallowTraceBuilder(),
              _predecessor);
        }
        propagate(traceContext, result);
        return result;
      } , true);
      context.after(_task).run(propagationTask);
      context.run(_task);
    }
    return result;
  }

  public static <S, T> FusionTask<?, T> fuse(final String name, final Task<S> task,
      final PromisePropagator<S, T> propagator, final ShallowTraceBuilder predecessor) {
    return fuse(name, task, withFusionTraceContext(propagator), predecessor);
  }
}
