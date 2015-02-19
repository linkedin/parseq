package com.linkedin.parseq.function;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Tuple4<T1, T2, T3, T4> implements Tuple {

  private final T1 _1;
  private final T2 _2;
  private final T3 _3;
  private final T4 _4;

  public Tuple4(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
    _1 = t1;
    _2 = t2;
    _3 = t3;
    _4 = t4;
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
  public T4 _4() {
    return _4;
  }

  public <C> C map(final Function4<T1, T2, T3, T4, C> f) {
    return f.apply(_1, _2, _3, _4);
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
          case 3:
            _index++;
            return _4;
        }
        throw new NoSuchElementException();
      }
    };
  }

  @Override
  public int arity() {
    return 4;
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Tuple4) {
          Tuple4<?, ?, ?, ?> that = (Tuple4<?, ?, ?, ?>) other;
          return Objects.equals(this._1, that._1)
                  && Objects.equals(this._2, that._2)
                  && Objects.equals(this._3, that._3)
                  && Objects.equals(this._4, that._4);
      } else {
          return false;
      }
  }

  @Override
  public int hashCode() {
      return Objects.hash(_1, _2, _3, _4);
  }

  @Override
  public String toString() {
      return "("
              + Objects.toString(_1)
       + ", " + Objects.toString(_2)
       + ", " + Objects.toString(_3)
       + ", " + Objects.toString(_4)
              + ")";
  }

}
