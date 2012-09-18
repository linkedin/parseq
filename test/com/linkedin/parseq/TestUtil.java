/*
 * Copyright 2012 LinkedIn, Inc
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

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;

import static com.linkedin.parseq.Tasks.action;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestUtil
{
  private TestUtil() {}

  public static Task<?> noop()
  {
    return action("noop", new Runnable()
    {
      @Override
      public void run()
      {
      }
    });
  }

  public static <T> Task<T> errorTask(final String name, final Exception error)
  {
    return new BaseTask<T>(name)
    {
      @Override
      public Promise<T> run(final Context context) throws Exception
      {
        throw error;
      }
    };
  }

  public static void assertThrows(final Class<? extends Exception> exceptionClass,
                                  final ThrowingRunnable runnable)
  {
    try
    {
      runnable.run();
    }
    catch (Exception e)
    {
      assertTrue("Expected exception: " + exceptionClass.getName() + " but got: " + e.getClass().getName(),
                 exceptionClass.isInstance(e));
      return;
    }
    fail("Should have thrown " + exceptionClass.getName());
  }

  public static <T> Task<T> value(final T value)
  {
    return value("value", value);
  }

  public static <T> Task<T> value(final String name, final T value)
  {
    return new ValueTask<T>(name, value);
  }

  private static class ValueTask<T> extends BaseTask<T>
  {
    private final T _value;

    public ValueTask(final String name, final T value)
    {
      super(name);
      _value = value;
    }

    @Override
    protected Promise<? extends T> run(final Context context) throws Exception
    {
      return Promises.value(_value);
    }
  }
}
