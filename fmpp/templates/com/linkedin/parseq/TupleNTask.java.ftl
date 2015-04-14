<#include "../../../macros/macros.ftl">
<@pp.dropOutputFile />
<#list 2..max as i>
<@pp.changeOutputFile name="Tuple" + i + "Task.java" />
package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer${i};
import com.linkedin.parseq.function.Function${i};
import com.linkedin.parseq.function.Tuple${i};

public interface Tuple${i}Task<<@typeParameters i/>> extends Task<Tuple${i}<<@typeParameters i/>>> {

  default <R> Task<R> map(final Function${i}<<@typeParameters i/>, R> f) {
    return map(tuple -> f.apply(<@tupleArgs i/>));
  }

  default <R> Task<R> map(final String desc, final Function${i}<<@typeParameters i/>, R> f) {
    return map(desc, tuple -> f.apply(<@tupleArgs i/>));
  }

  default <R> Task<R> flatMap(final Function${i}<<@typeParameters i/>, Task<R>> f) {
    return flatMap(tuple -> f.apply(<@tupleArgs i/>));
  }

  default <R> Task<R> flatMap(final String desc, final Function${i}<<@typeParameters i/>, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(<@tupleArgs i/>));
  }

  default Tuple${i}Task<<@typeParameters i/>> andThen(final Consumer${i}<<@typeParameters i/>> consumer) {
    return cast(andThen(tuple -> consumer.accept(<@tupleArgs i/>)));
  }

  default Tuple${i}Task<<@typeParameters i/>> andThen(final String desc, final Consumer${i}<<@typeParameters i/>> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(<@tupleArgs i/>)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple${i}Task<<@typeParameters i/>> recover(final Function1<Throwable, Tuple${i}<<@typeParameters i/>>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple${i}Task<<@typeParameters i/>> recover(final String desc, final Function1<Throwable, Tuple${i}<<@typeParameters i/>>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple${i}Task<<@typeParameters i/>> recoverWith(final Function1<Throwable, Task<Tuple${i}<<@typeParameters i/>>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple${i}Task<<@typeParameters i/>> recoverWith(final String desc, final Function1<Throwable, Task<Tuple${i}<<@typeParameters i/>>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple${i}Task<<@typeParameters i/>> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple${i}Task<<@typeParameters i/>> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple${i}Task<<@typeParameters i/>> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple${i}Task<<@typeParameters i/>> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  default Tuple${i}Task<<@typeParameters i/>> withSideEffect(Function${i}<<@typeParameters i/>, Task<?>> func) {
    return cast(Task.super.withSideEffect(tuple -> func.apply(<@tupleArgs i/>)));
  }

  default Tuple${i}Task<<@typeParameters i/>> withSideEffect(final String desc, Function${i}<<@typeParameters i/>, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(<@tupleArgs i/>)));
  }

  public static <<@typeParameters i/>> Tuple${i}Task<<@typeParameters i/>> cast(final Task<Tuple${i}<<@typeParameters i/>>> task) {
    return new Tuple${i}TaskDelegate<>(task);
  }

}
</#list>
