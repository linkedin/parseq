package com.linkedin.parseq.function;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Tuple2<T1, T2> implements Tuple {

  private final T1 _1;
  private final T2 _2;

  public Tuple2(final T1 t1, final T2 t2) {
    _1 = t1;
    _2 = t2;
  }

  public T1 _1() {
    return _1;
  }
  public T2 _2() {
    return _2;
  }

  public <C> C map(final Function2<T1, T2, C> f) throws Exception {
    return f.apply(_1, _2);
  }

  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int _index = 0;
      @Override
      public boolean hasNext() {
        return _index < arity();
      }

      @Override
      public Object next() {
        switch(_index) {
          case 0:
            _index++;
            return _1;
          case 1:
            _index++;
            return _2;
        }
        throw new NoSuchElementException();
      }
    };
  }

  @Override
  public int arity() {
    return 2;
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Tuple2) {
          Tuple2<?, ?> that = (Tuple2<?, ?>) other;
          return Objects.equals(this._1, that._1)
                  && Objects.equals(this._2, that._2);
      } else {
          return false;
      }
  }

  @Override
  public int hashCode() {
      return Objects.hash(_1, _2);
  }

  @Override
  public String toString() {
      return "("
              + Objects.toString(_1)
       + ", " + Objects.toString(_2)
              + ")";
  }

}
