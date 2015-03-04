<#include "../../../macros/macros.ftl">
<@pp.dropOutputFile />
<#list 2..max as i>
<@pp.changeOutputFile name="Par" + i + "Task.java" />
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

import com.linkedin.parseq.function.Function${i};
import com.linkedin.parseq.function.Tuple${i};
import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.internal.SystemHiddenTask;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

public class Par${i}Task<<@typeParameters i/>> extends SystemHiddenTask<Tuple${i}<<@typeParameters i/>>> implements Tuple${i}Task<<@typeParameters i/>>
{
  private final Tuple${i}<<@csv 1..i; j>Task<T${j}></@csv>> _tasks;

  public Par${i}Task(final String name, <@csv 1..i; j>Task<T${j}> task${j}</@csv>)
  {
    super(name);
    _tasks = tuple(<@csv 1..i; j>task${j}</@csv>);
  }

  @Override
  protected Promise<Tuple${i}<<@typeParameters i/>>> run(final Context context) throws Exception
  {
    final SettablePromise<Tuple${i}<<@typeParameters i/>>> result = Promises.settable();

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
<@csvNL 2..i; j>                          _tasks._${j}().get()</@csvNL>));
      }
    },
<@csvNL 1..i; j>    _tasks._${j}()</@csvNL>);

    _tasks.forEach(t -> context.run((Task<?>)t));

    return result;
  }

  public <R> Task<R> map(final Function${i}<<@typeParameters i/>, R> f) {
    return map(tuple -> f.apply(<@tupleArgs i/>));
  }


}
</#list>
