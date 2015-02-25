package com.linkedin.parseq.collection.async;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.linkedin.parseq.collection.GroupedParSeqCollection;
import com.linkedin.parseq.collection.ParSeqCollection;
import com.linkedin.parseq.collection.transducer.Foldable;
import com.linkedin.parseq.collection.transducer.Transducer;
import com.linkedin.parseq.collection.transducer.Transducible;
import com.linkedin.parseq.collection.transducer.Reducer.Step;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.TaskOrValue;

public class AsyncCollection<T, R> extends Transducible<T, R> implements ParSeqCollection<R> {

  private final TaskOrValue<Step<Object>> CONTINUE = TaskOrValue.value(Step.cont(Optional.empty()));

  private final Publisher<TaskOrValue<T>> _source;
  private final Optional<Task<?>> _predecessor;

  protected AsyncCollection(Publisher<TaskOrValue<T>> source, Transducer<T, R> transducer, Optional<Task<?>> predecessor) {
    super(transducer);
    _source = source;
    _predecessor = predecessor;
  }

  private <Z> Foldable<Z, T, Task<Z>> foldable() {
    return new AsyncFoldable<Z, T>(_source, _predecessor);
  }

  private <A, B> AsyncCollection<A, B> createStreamCollection(Publisher<TaskOrValue<A>> source, Transducer<A, B> transducer) {
    return new AsyncCollection<A, B>(source, transducer, _predecessor);
  }

  private <B> AsyncCollection<T, B> create(Transducer<T, B> transducer) {
    return createStreamCollection(_source, transducer);
  }

  @Override
  public <A> ParSeqCollection<A> map(final Function<R, A> f) {
    return map(f, this::create);
  }

  @Override
  public ParSeqCollection<R> forEach(final Consumer<R> consumer) {
    return forEach(consumer, this::create);
  }

  @Override
  public ParSeqCollection<R> filter(final Predicate<R> predicate) {
    return filter(predicate, this::create);
  }

  @Override
  public ParSeqCollection<R> take(final int n) {
    return take(n, this::create);
  }

  @Override
  public ParSeqCollection<R> takeWhile(final Predicate<R> predicate) {
    return takeWhile(predicate, this::create);
  }

  @Override
  public ParSeqCollection<R> drop(final int n) {
    return drop(n, this::create);
  }

  @Override
  public ParSeqCollection<R> dropWhile(final Predicate<R> predicate) {
    return dropWhile(predicate, this::create);
  }

  /*
   * Foldings:
   */

  @Override
  public <Z> Task<Z> fold(final Z zero, final BiFunction<Z, R, Z> op) {
    return fold(zero, op, foldable());
  }

  @Override
  public Task<R> first() {
    return checkEmptyAsync(first(foldable()));
  }

  @Override
  public Task<R> last() {
    return checkEmptyAsync(last(foldable()));
  }

  @Override
  public Task<List<R>> toList() {
    return toList(foldable());
  }

  @Override
  public Task<R> reduce(final BiFunction<R, R, R> op) {
    return checkEmptyAsync(reduce(op, foldable()));
  }

  @Override
  public Task<R> find(final Predicate<R> predicate) {
    return checkEmptyAsync(find(predicate, foldable()));
  }

  @Override
  public Task<?> task() {
    return foldable().fold("task", Optional.empty(), transduce((z, r) -> r.map(rValue -> {
      return Step.cont(z);
    })));
  }

  @Override
  public <A> ParSeqCollection<A> mapTask(final Function<R, Task<A>> f) {
    return create(_transducer.map(r -> r.mapTask(f)));
  }

  @Override
  public <A> ParSeqCollection<A> flatMap(Function<R, ParSeqCollection<A>> f) {

    CancellableSubscription subscription = new CancellableSubscription();
    final PushablePublisher<Publisher<TaskOrValue<A>>> publisher = new PushablePublisher<Publisher<TaskOrValue<A>>>(subscription);

    @SuppressWarnings("unchecked")
    Task<?> publisherTask = mapTask(r -> {
      final PushablePublisher<TaskOrValue<A>> pushablePublisher = new PushablePublisher<TaskOrValue<A>>(subscription);
      publisher.next(pushablePublisher);
      return ((AsyncCollection<?, A>)f.apply(r)).publisherTask(pushablePublisher, subscription);
    }).task();
    publisherTask.onResolve(p -> {
      if (p.isFailed()) {
        publisher.error(p.getError());
      } else {
        publisher.complete();
      }
    });

    return new AsyncCollection<A, A>(Publisher.flatten(publisher), Transducer.identity(), Optional.of(publisherTask));
  }

  protected Task<?> publisherTask(final PushablePublisher<TaskOrValue<R>> pushable,
      final CancellableSubscription subscription) {
    final Task<?> fold = foldable().fold("toStream", Optional.empty(), transduce((z, r) -> {
      if (subscription.isCancelled()) {
        return TaskOrValue.value(Step.done(z));
      } else {
        pushable.next(r);
        return CONTINUE;
      }
    }));
    fold.onResolve(p -> {
      if (p.isFailed()) {
        pushable.error(p.getError());
      } else {
        pushable.complete();
      }
    });
    return fold;
  }

  @Override
  public <K> ParSeqCollection<GroupedParSeqCollection<K, R>> groupBy(Function<R, K> classifier) {
//  final Publisher<R> that = this;
//  return new Publisher<GroupedStreamCollection<A, R, R>>() {
//    private int groupCount = 0;
//
//    @Override
//    public void subscribe(final AckingSubscriber<GroupedStreamCollection<A, R, R>> subscriber) {
//
//      final Map<A, PushablePublisher<R>> publishers = new HashMap<A, PushablePublisher<R>>();
//      final Set<A> calcelledGroups = new HashSet<A>();
//
//      that.subscribe(new AckingSubscriber<R>() {
//
//        @Override
//        public void onNext(final AckValue<R> element) {
//          /**
//           * TODO
//           * Update documentation about ack: it is not a mechanism for backpressure:
//           * - is backpressure relevant problem for a processing finite streams?
//           * - ack is used to provide Seq semantics
//           *
//           * add try/catch to all those methods
//           */
//          final A group = classifier.apply(element.get());
//          if (calcelledGroups.contains(group)) {
//            element.ack(FlowControl.cont);
//          } else {
//            PushablePublisher<R> pub = publishers.get(group);
//            if (pub == null) {
//              final CancellableSubscription subscription = new CancellableSubscription();
//              pub = new PushablePublisher<R>(() -> {
//                subscription.cancel();
//                calcelledGroups.add(group);
//              });
//              publishers.put(group, pub);
//              subscriber.onNext(new AckValue<>(new GroupedStreamCollection<A, R, R>(group, pub, Transducer.identity()), Ack.NO_OP));
//              groupCount++;
//            }
//            //at this point subscription might have been already cancelled
//            if (!calcelledGroups.contains(group)) {
//              pub.next(element);
//            }
//          }
//        }
//
//        @Override
//        public void onComplete(final int totalTasks) {
//          subscriber.onComplete(groupCount);
//          for (PushablePublisher<R> pub: publishers.values()) {
//            pub.complete();
//          }
//        }
//
//        @Override
//        public void onError(Throwable cause) {
//          subscriber.onError(cause);
//          for (PushablePublisher<R> pub: publishers.values()) {
//            pub.error(cause);
//          }
//        }
//
//        @Override
//        public void onSubscribe(Subscription subscription) {
//          //we would be able to cancel stream if all groups cancelled their streams
//          //unfortunately we can't cancel stream because we don't know
//          //what elements are coming in the stream so we don't know list of all groups
//        }
//
//      });
//    }
//  }.collection();
    return null;
  }

  protected static final <R> Task<R> checkEmptyAsync(Task<Optional<R>> result) {
    return result.map("checkEmpty", Transducible::checkEmpty);
  }

  public static <A> ParSeqCollection<A> fromValues(final Iterable<A> iterable) {
    IterablePublisher<A, A> publisher = new ValuesPublisher<A>(iterable);
    Task<?> task = Task.action("values", publisher::run);
    task.onResolve(p -> publisher.complete(p));
    return new AsyncCollection<>(publisher, Transducer.identity(),
        Optional.of(task));
  }

  public static <A> ParSeqCollection<A> fromTasks(final Iterable<Task<A>> iterable) {
    IterablePublisher<Task<A>, A> publisher = new TasksPublisher<A>(iterable);
    Task<?> task = Task.action("tasks", publisher::run);
    task.onResolve(p -> publisher.complete(p));
    return new AsyncCollection<>(publisher, Transducer.identity(),
        Optional.of(task));
  }

  @Override
  public Task<Integer> count() {
    return fold(0, (count, e) -> count + 1);
  }

  @Override
  public ParSeqCollection<R> within(long time, TimeUnit unit) {
    //TODO within on publisher task is not enough - need to wrap future folding task
//    CancellableSubscription subscription = new CancellableSubscription();
//    final PushablePublisher<TaskOrValue<R>> publisher = new PushablePublisher<TaskOrValue<R>>(subscription);
//    Task<?> task = publisherTask(publisher, subscription).within(time, unit);
//    return new AsyncCollection<>(publisher, Transducer.identity(), Optional.of(task));
    throw new java.lang.UnsupportedOperationException("not implemented yet");
  }

  @Override
  public Task<?> subscribe(final Subscriber<R> subscriber) {
    final CancellableSubscription subscription = new CancellableSubscription();
    Task<?> fold = foldable().fold("stream", Optional.empty(), transduce((z, e) -> e.map(eValue -> {
      if (!subscription.isCancelled()) {
        subscriber.onNext(eValue);
        return Step.cont(z);
      } else {
        return Step.done(z);
      }
    })));
    fold.onResolve(p -> {
      if (p.isFailed()) {
        subscriber.onError(p.getError());
      } else {
        subscriber.onComplete();
      }
    });
    return Task.action("onSubscribe", () -> subscriber.onSubscribe(subscription)).andThen(fold);
  }

  @Override
  public ParSeqCollection<R> distinct() {
    final HashSet<R> distinctFilter = new HashSet<>();
    return filter(r -> distinctFilter.add(r));
  }

  @Override
  public Task<R> max(final Comparator<? super R> comparator) {
    return reduce((a, b) -> comparator.compare(a, b) <= 0 ? b : a);
  }

  @Override
  public Task<R> min(final Comparator<? super R> comparator) {
    return reduce((a, b) -> comparator.compare(a, b) <= 0 ? a : b);
  }

  @Override
  public ParSeqCollection<R> sorted(final Comparator<? super R> comparator) {
    final Task<List<R>> sortedList = toList();
    IterablePublisher<R, R> publisher = new IterablePublisher<R, R>(TaskOrValue::value) {
      @Override
      Iterable<R> getElements() {
        return sortedList.get();
      }
    };
    Task<?> task = sortedList.andThen("sortedValues", l -> {
      Collections.sort(l, comparator);
      publisher.run();
    });
    task.onResolve(p -> publisher.complete(p));
    return new AsyncCollection<>(publisher, Transducer.identity(),
        Optional.of(task));
  }

}