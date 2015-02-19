package com.linkedin.parseq.function;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple {

  private final T1 _1;
  private final T2 _2;
  private final T3 _3;
  private final T4 _4;
  private final T5 _5;
  private final T6 _6;
  private final T7 _7;
  private final T8 _8;
  private final T9 _9;

  public Tuple9(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8, final T9 t9) {
    _1 = t1;
    _2 = t2;
    _3 = t3;
    _4 = t4;
    _5 = t5;
    _6 = t6;
    _7 = t7;
    _8 = t8;
    _9 = t9;
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
  public T5 _5() {
    return _5;
  }
  public T6 _6() {
    return _6;
  }
  public T7 _7() {
    return _7;
  }
  public T8 _8() {
    return _8;
  }
  public T9 _9() {
    return _9;
  }

  public <C> C map(final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> f) {
    return f.apply(_1, _2, _3, _4, _5, _6, _7, _8, _9);
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
          case 4:
            _index++;
            return _5;
          case 5:
            _index++;
            return _6;
          case 6:
            _index++;
            return _7;
          case 7:
            _index++;
            return _8;
          case 8:
            _index++;
            return _9;
        }
        throw new NoSuchElementException();
      }
    };
  }

  @Override
  public int arity() {
    return 9;
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Tuple9) {
          Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> that = (Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) other;
          return Objects.equals(this._1, that._1)
                  && Objects.equals(this._2, that._2)
                  && Objects.equals(this._3, that._3)
                  && Objects.equals(this._4, that._4)
                  && Objects.equals(this._5, that._5)
                  && Objects.equals(this._6, that._6)
                  && Objects.equals(this._7, that._7)
                  && Objects.equals(this._8, that._8)
                  && Objects.equals(this._9, that._9);
      } else {
          return false;
      }
  }

  @Override
  public int hashCode() {
      return Objects.hash(_1, _2, _3, _4, _5, _6, _7, _8, _9);
  }

  @Override
  public String toString() {
      return "("
              + Objects.toString(_1)
       + ", " + Objects.toString(_2)
       + ", " + Objects.toString(_3)
       + ", " + Objects.toString(_4)
       + ", " + Objects.toString(_5)
       + ", " + Objects.toString(_6)
       + ", " + Objects.toString(_7)
       + ", " + Objects.toString(_8)
       + ", " + Objects.toString(_9)
              + ")";
  }

}
