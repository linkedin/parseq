<#include "../../../../macros/macros.ftl">
<@pp.dropOutputFile />
<#list 2..max as i>
<@pp.changeOutputFile name="Tuple" + i + ".java" />
package com.linkedin.parseq.function;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Tuple${i}<<@typeParameters i/>> implements Tuple {

<@repeat 1..i; j>
  private final T${j} _${j};
</@repeat>

  public Tuple${i}(<@parameters i/>) {
<@repeat 1..i; j>
    _${j} = t${j};
</@repeat>
  }

<@repeat 1..i; j>
  public T${j} _${j}() {
    return _${j};
  }
</@repeat>

  public <C> C map(final Function${i}<<@typeParameters i/>, C> f) throws Exception {
    return f.apply(<@csv 1..i; j>_${j}</@csv>);
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
<@repeat 1..i; j>
          case ${j-1}:
            _index++;
            return _${j};
</@repeat>
        }
        throw new NoSuchElementException();
      }
    };
  }

  @Override
  public int arity() {
    return ${i};
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Tuple${i}) {
          Tuple${i}<<@csv 1..i; j>?</@csv>> that = (Tuple${i}<<@csv 1..i; j>?</@csv>>) other;
          return Objects.equals(this._1, that._1)
<@lines 2..i; j>                  && Objects.equals(this._${j}, that._${j})</@lines>;
      } else {
          return false;
      }
  }

  @Override
  public int hashCode() {
      return Objects.hash(<@csv 1..i; j>_${j}</@csv>);
  }

  @Override
  public String toString() {
      return "("
              + Objects.toString(_1)
<@repeat 2..i; j>
       + ", " + Objects.toString(_${j})
</@repeat>
              + ")";
  }

}
</#list>
