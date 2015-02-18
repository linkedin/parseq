package com.linkedin.parseq.function;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;

public class Tuple3<T1, T2, T3> implements Tuple {

  private final T1 _1;
  private final T2 _2;
  private final T3 _3;

  public Tuple3(T1 param1, T2 param2, T3 param3) {
    _1 = param1;
    _2 = param2;
    _3 = param3;
  }

  public T1 _1() {
    return _1;
  }

  public T2 _2() {
    return _2;
  }

  public T3 _3() {
    return _3;
  }

  public <C> C map(final Function3<T1, T2, T3, C> f) {
    return f.apply(_1, _2, _3);
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
          case 2:
            _index++;
            return _3;
        }
        throw new NoSuchElementException();
      }
    };
  }

  @Override
  public int arity() {
    return 3;
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Tuple3) {
          Tuple3<?, ?, ?> that = (Tuple3<?, ?, ?>) other;
          return Objects.equals(this._1, that._1)
                  && Objects.equals(this._2, that._2)
                  && Objects.equals(this._3, that._3);
      } else {
          return false;
      }
  }

  @Override
  public int hashCode() {
      return Objects.hash(_1, _2, _3);
  }

  @Override
  public String toString() {
      return "("
              + Objects.toString(_1) + ", "
              + Objects.toString(_2) + ", "
              + Objects.toString(_3)
              + ")";
  }

}
