<#include "../../../../macros/macros.ftl">
<@pp.dropOutputFile />
<@pp.changeOutputFile name="Tuples.java" />
package com.linkedin.parseq.function;

public class Tuples {
  private Tuples() {}

<@lines 2..max; i>  public static <<@typeParameters i/>> Tuple${i}<<@typeParameters i/>> tuple(<@parameters i/>) {
    return new Tuple${i}<<@typeParameters i/>>(<@csv 1..i; j>t${j}</@csv>);
  }
</@lines>

}
