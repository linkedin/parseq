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

package com.linkedin.parseq.internal;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class ArgumentUtil {
  private ArgumentUtil() {
  }

  public static void requireNotNull(final Object obj, final String name) {
    if (obj == null) {
      throw new NullPointerException(name + " must not be null");
    }
  }

  public static void requireNotEmpty(final String str, final String name) {
    requireNotNull(str, name);
    if (str.isEmpty()) {
      throw new IllegalArgumentException(name + " is an empty string");
    }
  }

  public static void requirePositive(final int n, final String name) {
    if (n <= 0) {
      throw new IllegalArgumentException(name + " must be a positive integer number, but is: " + n);
    }
  }

  public static void requirePositive(final long n, final String name) {
    if (n <= 0) {
      throw new IllegalArgumentException(name + " must be a positive long number, but is: " + n);
    }
  }
}
