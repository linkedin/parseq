package com.linkedin.parseq.collection.transducer;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.linkedin.parseq.collection.transducer.Reducer.Step;

/**
 * TODO merge this back to the single implementation
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <T>
 * @param <R>
 */
public abstract class Transducible<T, R> {
  /**
   * This function transforms folding function from the one which folds type R to the one
   * which folds type T.
   */
  protected final Transducer<T, R> _transducer;

  protected Transducible(Transducer<T, R> transducer) {
    _transducer = transducer;
  }

  @SuppressWarnings("unchecked")
  protected <Z> Reducer<Z, T> transduce(final Reducer<Z, R> reducer) {
    return (Reducer<Z, T>)_transducer.apply((Reducer<Object, R>)reducer);
  }

  protected static final <R> R checkEmpty(Optional<R> result) {
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new NoSuchElementException();
    }
  }

  /*
   * Collection transformations:
   */

  protected <A, V extends Transducible<T, A>> V map(final Function<R, A> f,
      final Function<Transducer<T, A>, V> collectionBuilder) {
    return collectionBuilder.apply(_transducer.map(r -> r.map(f)));
  }

  protected <V extends Transducible<T, R>> V forEach(final Consumer<R> consumer,
      final Function<Transducer<T, R>, V> collectionBuilder) {
    return map(e -> {
      consumer.accept(e);
      return e;
    }, collectionBuilder);
  }

  protected <V extends Transducible<T, R>> V filter(final Predicate<R> predicate,
      final Function<Transducer<T, R>, V> collectionBuilder) {
    return collectionBuilder.apply(_transducer.filter(predicate));
  }

  protected <V extends Transducible<T, R>> V take(final int n,
      final Function<Transducer<T, R>, V> collectionBuilder) {
    return collectionBuilder.apply(_transducer.take(n));
  }

  protected <V extends Transducible<T, R>> V takeWhile(final Predicate<R> predicate,
      final Function<Transducer<T, R>, V> collectionBuilder) {
    return collectionBuilder.apply(_transducer.takeWhile(predicate));
  }

  protected <V extends Transducible<T, R>> V drop(final int n,
      final Function<Transducer<T, R>, V> collectionBuilder) {
    return collectionBuilder.apply(_transducer.drop(n));
  }

  protected <V extends Transducible<T, R>> V dropWhile(final Predicate<R> predicate,
      final Function<Transducer<T, R>, V> collectionBuilder) {
    return collectionBuilder.apply(_transducer.dropWhile(predicate));
  }

  /*
   * Foldings:
   */

  protected <Z, V> V fold(final Z zero, final BiFunction<Z, R, Z> op, final Foldable<Z, T, V> foldable) {
    return foldable.fold("fold", zero, transduce((z, e) -> e.map(eValue -> Step.cont(op.apply(z.refGet(), eValue)))));
  }

  protected <V> V first(final Foldable<Optional<R>, T, V> foldable) {
    return foldable.fold("first", Optional.empty(), transduce((z, r) -> r.map(rValue -> Step.done(Optional.of(rValue)))));
  }

  protected <V> V last(final Foldable<Optional<R>, T, V> foldable) {
    return foldable.fold("last", Optional.empty(), transduce((z, r) -> r.map(rValue -> Step.cont(Optional.of(rValue)))));
  }

  protected <V> V toList(final Foldable<List<R>, T, V> foldable) {
    return foldable.fold("toList", new ArrayList<R>(), transduce((z, r) -> r.map(rValue -> {
      z.refGet().add(rValue);
      return Step.cont(z.refGet());
    })));
  }

  protected <V> V reduce(final BiFunction<R, R, R> op, final Foldable<Optional<R>, T, V> foldable) {
    return foldable.fold("reduce", Optional.empty(), transduce((z, e) -> e.map(eValue -> {
      if (z.refGet().isPresent()) {
        return Step.cont(Optional.of(op.apply(z.refGet().get(), eValue)));
      } else {
        return Step.cont(Optional.of(eValue));
      }
    })));
  }

  protected <V> V find(final Predicate<R> predicate, final Foldable<Optional<R>, T, V> foldable) {
    return foldable.fold("find", Optional.empty(), transduce((z, e) -> e.map(eValue -> {
      if (predicate.test(eValue)) {
        return Step.done(Optional.of(eValue));
      } else {
        return Step.cont(z.refGet());
      }
    })));
  }

}
