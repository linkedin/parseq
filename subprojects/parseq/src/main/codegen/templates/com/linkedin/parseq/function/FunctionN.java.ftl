<#include "../../../../macros/macros.ftl">
<@pp.dropOutputFile />
<#list 2..max as i>
<@pp.changeOutputFile name="Function" + i + ".java" />
package com.linkedin.parseq.function;

import java.util.Objects;
import com.linkedin.parseq.function.Function1;

@FunctionalInterface
public interface Function${i}<<@typeParameters i/>, R> {

    R apply(<@csv 1..i; j>T${j} t${j}</@csv>) throws Exception;

    default <V> Function${i}<<@typeParameters i/>, V> andThen(Function1<? super R, ? extends V> f) {
        Objects.requireNonNull(f);
        return (<@csv 1..i; j>T${j} t${j}</@csv>) ->
          f.apply(apply(<@csv 1..i; j>t${j}</@csv>));
    }

}
</#list>
