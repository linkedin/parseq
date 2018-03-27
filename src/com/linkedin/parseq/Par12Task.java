/*
 * Copyright 2015 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq;

import static com.linkedin.parseq.function.Tuples.tuple;

import com.linkedin.parseq.function.Tuple12;
import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

public class Par12Task<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> extends BaseTask<Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> implements Tuple12Task<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> {
  private final Tuple12<Task<T1>, Task<T2>, Task<T3>, Task<T4>, Task<T5>, Task<T6>, Task<T7>, Task<T8>, Task<T9>, Task<T10>, Task<T11>, Task<T12>> _tasks;

  public Par12Task(final String name, Task<T1> task1, Task<T2> task2, Task<T3> task3, Task<T4> task4, Task<T5> task5, Task<T6> task6, Task<T7> task7, Task<T8> task8, Task<T9> task9, Task<T10> task10, Task<T11> task11, Task<T12> task12) {
    super(name);
    _tasks = tuple(task1, task2, task3, task4, task5, task6, task7, task8, task9, task10, task11, task12);
  }

  @Override
  protected Promise<Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> run(final Context context) throws Exception {
    final SettablePromise<Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> result = Promises.settable();

    InternalUtil.fastFailAfter(p -> {
      if (p.isFailed()) {
        result.fail(p.getError());
      } else {
        result.done(tuple(_tasks._1().get(),
                          _tasks._2().get(),
                          _tasks._3().get(),
                          _tasks._4().get(),
                          _tasks._5().get(),
                          _tasks._6().get(),
                          _tasks._7().get(),
                          _tasks._8().get(),
                          _tasks._9().get(),
                          _tasks._10().get(),
                          _tasks._11().get(),
                          _tasks._12().get()));
      }
    },
    _tasks._1(),
    _tasks._2(),
    _tasks._3(),
    _tasks._4(),
    _tasks._5(),
    _tasks._6(),
    _tasks._7(),
    _tasks._8(),
    _tasks._9(),
    _tasks._10(),
    _tasks._11(),
    _tasks._12());

    _tasks.forEach(t -> context.run((Task<?>)t));

    return result;
  }

}
