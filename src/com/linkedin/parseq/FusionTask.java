package com.linkedin.parseq;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.internal.ArgumentUtil;
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
 * TODO fix withtimeout - it's synchronous
 *
 *
 * @author jodzga
 *
 * @param <S>
 * @param <T>
 */
public class FusionTask<S, T>  extends BaseTask<T> {

  private final PromisePropagator<S, T> _propagator;
  private final Task<S> _task;
  private final Promise<S> _source;


  private final AtomicReference<FusionTraceContext> _traceContextRef;
  private final Optional<ShallowTraceBuilder> _predecessor;

  private FusionTask(final String desc, final Task<S> task, final Promise<S> source,
      final PromisePropagator<S, T> propagator, final boolean systemHidden,
      final AtomicReference<FusionTraceContext> traceContextRef, final Optional<ShallowTraceBuilder> predecessor) {
    super(desc);
    _propagator = completing(propagator);
    _task = task;
    _source = source;
    _shallowTraceBuilder.setSystemHidden(systemHidden);
    _traceContextRef = traceContextRef;
    _predecessor = predecessor;
  }

  @Override
  public TraceBuilder getTraceBuilder() {
    if (_traceContextRef.get() != null) {
    return _traceContextRef.get().getTraceBuilder();
    } else {
      return super.getTraceBuilder();
    }
  }

  private void trnasitionToDone(final FusionTraceContext traceContext) {
    addRelationships(traceContext);
    transitionPending();
    transitionDone();
  }

  private void addRelationships(final FusionTraceContext traceContext) {
    TraceBuilder builder = getTraceBuilder();
    if (traceContext.getParent() != null &&
        traceContext.getParent().getId() != getId()) {
      builder.addRelationship(Relationship.PARENT_OF, traceContext.getParent(), _shallowTraceBuilder);
    }
    if (_predecessor.isPresent() &&
        (traceContext.getParent() == null || traceContext.getParent().getId() != getId())) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, _predecessor.get());
    } else if (traceContext.getSource() != null &&
        (traceContext.getParent() == null || traceContext.getParent().getId() != getId())) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, traceContext.getSource());
    } else if (_predecessor.isPresent() &&
        (traceContext.getParent() == null ||
        (traceContext.getParent().getId() == getId() &&
        !builder.containsRelationship(new TraceRelationship(getId(), _predecessor.get().getId(), Relationship.PARENT_OF))))) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, _predecessor.get());
    }
  }

  private PromisePropagator<S, T> completing(final PromisePropagator<S, T> propagator) {
    return (src, dest) -> {
      final FusionTraceContext traceContext = _traceContextRef.get();
      final SettablePromise<T> settable = FusionTask.this.getSettableDelegate();

      if ((traceContext.getParent() != null && traceContext.getParent().getId() == getId()) || transitionRun(getTraceBuilder())) {
        traceContext.getTaskLogger().logTaskStart(this);
        propagator.accept(src, new Settable<T>() {
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
            } catch (Exception e) {
              dest.fail(e);
            } finally {
              dest.done(value);
            }
          }

          @Override
          public void fail(final Throwable error) throws PromiseResolvedException {
            try {
              trnasitionToDone(traceContext);
              if (error instanceof EarlyFinishException) {
                _shallowTraceBuilder.setResultType(ResultType.EARLY_FINISH);
              } else {
                _shallowTraceBuilder.setResultType(ResultType.ERROR);
                _shallowTraceBuilder.setValue(error.toString());
              }
              settable.fail(error);
              traceContext.getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
            } finally {
              dest.fail(error);
            }
          }
        });
      } else {
        Promises.propagateResult(settable, dest);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <S, T> FusionTask<?, T> fuse(final String name, final Task<S> task,
      final PromisePropagator<S, T> propagator, final AtomicReference<FusionTraceContext> traceContextRef,
      final Optional<ShallowTraceBuilder> predecessor) {
    if (task instanceof FusionTask) {
      return ((FusionTask<?, S>) task).apply(name, propagator);
    } else {
      return new FusionTask<S, T>(name, task, task, propagator, true, traceContextRef, predecessor);
    }
  }

  public static <S, T> FusionTask<?, T> create(final String name, final Promise<S> source, final PromisePropagator<S, T> propagator) {
    return new FusionTask<S, T>(name, null, source, propagator, false, new AtomicReference<>(), Optional.empty());
  }

  @Override
  public <R> FusionTask<?, R> apply(String desc, PromisePropagator<T,R> propagator) {
    return fuse(desc, _task, _propagator.compose(propagator), _traceContextRef, Optional.of(_shallowTraceBuilder));
  };

  @Override
  public Task<T> recoverWith(final String desc, final Function1<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return Task.async(desc, context -> {
      final SettablePromise<T> result = Promises.settable();
      context.after(that).run(() -> {
        if (that.isFailed() && !(that.getError() instanceof EarlyFinishException)) {
          try {
            Task<T> r = func.apply(that.getError());
            Promises.propagateResult(r, result);
            return Optional.of(r);
          } catch (Throwable t) {
            result.fail(t);
            return Optional.empty();
          }
        } else {
          result.done(that.get());
          return Optional.empty();
        }
      });
      context.run(that);
      return result;
    }, true);
  }

  protected SettablePromise<T> propagate(final FusionTraceContext traceContext, final SettablePromise<T> result) {
    try {
      _traceContextRef.set(traceContext);
      _propagator.accept(_source, result);
    } catch (Throwable t) {
      result.fail(t);
    }
    return result;
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Throwable {
    final SettablePromise<T> result = Promises.settable();
    if (_task == null) {
      FusionTraceContext traceContext = new FusionTraceContext(context.getTraceBuilder(), null,
          FusionTask.this.getShallowTraceBuilder(), context.getTaskLogger());
      propagate(traceContext, result);
    } else {
      final Task<? extends T> propagationTask =
          Task.async(getName(), ctx -> {
            FusionTraceContext traceContext =
                new FusionTraceContext(ctx.getTraceBuilder(), _task.getShallowTraceBuilder(),
                    FusionTask.this.getShallowTraceBuilder(), ctx.getTaskLogger());
            if (_predecessor.isPresent()) {
              ctx.getTraceBuilder().addRelationship(Relationship.SUCCESSOR_OF, ctx.getShallowTraceBuilder(), _predecessor.get());
            }
            return propagate(traceContext, result);
          }, false);
      context.after(_task).run(propagationTask);
      context.run(_task);
    }
    return result;
  }
}
