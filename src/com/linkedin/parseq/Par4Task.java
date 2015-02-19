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

import java.util.ArrayList;
import java.util.List;

import com.linkedin.parseq.function.Function4;
import com.linkedin.parseq.function.Tuple4;
import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.internal.SystemHiddenTask;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

public class Par4Task<T1, T2, T3, T4> extends SystemHiddenTask<Tuple4<T1, T2, T3, T4>> implements Tuple4Task<T1, T2, T3, T4>
{
  private final Tuple4<Task<T1>, Task<T2>, Task<T3>, Task<T4>> _tasks;

  public Par4Task(final String name, Task<T1> task1, Task<T2> task2, Task<T3> task3, Task<T4> task4)
  {
    super(name);
    _tasks = tuple(task1, task2, task3, task4);
  }

  @Override
  protected Promise<Tuple4<T1, T2, T3, T4>> run(final Context context) throws Exception
  {
    final SettablePromise<Tuple4<T1, T2, T3, T4>> result = Promises.settable();

    InternalUtil.after(p -> {
      final List<Throwable> errors = new ArrayList<Throwable>();
      _tasks.forEach(o -> {
        Task<?> t = (Task<?>)o;
        if (t.isFailed()) {
          errors.add(t.getError());
        }
      });
      if (!errors.isEmpty()) {
        result.fail(errors.size() == 1 ? errors.get(0) : new MultiException("Multiple errors in parallel task.", errors));
      } else {
        result.done(tuple(_tasks._1().get(),
                          _tasks._2().get(),
                          _tasks._3().get(),
                          _tasks._4().get()));
      }
    },
    _tasks._1(),
    _tasks._2(),
    _tasks._3(),
    _tasks._4());

    _tasks.forEach(t -> context.run((Task<?>)t));

    return result;
  }

  public <R> Task<R> map(final Function4<T1, T2, T3, T4, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4()));
  }


}
