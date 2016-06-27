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

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestUtil {
  private TestUtil() {
  }

  public static Task<?> noop() {
    return Task.action("noop", () -> {
    } );
  }

  public static void assertThrows(final Class<? extends Exception> exceptionClass, final ThrowingRunnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      assertTrue("Expected exception: " + exceptionClass.getName() + " but got: " + e.getClass().getName(),
          exceptionClass.isInstance(e));
      return;
    }
    fail("Should have thrown " + exceptionClass.getName());
  }

  public static void withDisabledLogging(final Runnable r) {
    // Note: this assumes we're using Log4J and needs to be fixed up if this changes.
    final LoggerRepository loggerRepo = LogManager.getLoggerRepository();
    final Level oldLevel = loggerRepo.getThreshold();
    loggerRepo.setThreshold(Level.OFF);
    try {
      r.run();
    } finally {
      loggerRepo.setThreshold(oldLevel);
    }
  }
}
