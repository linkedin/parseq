/*
 * Copyright 2019 LinkedIn, Inc
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

package com.linkedin.parseq.junitjupiter;

import com.linkedin.parseq.AbstractBaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.ParSeqUnitTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


/**
 * A base class that builds an Engine with default configuration.
 * Intended to be used with JUnit Jupiter (JUnit5+)
 *
 * This class creates new Engine and shuts it down before and after every test method, so it can't be used
 * to run tests in parallel.
 *
 * The difference between this class and {@link BaseEngineJunitJupiterTest} is that the latter creates a new
 * {@code Engine} instance only once for all tests in the class and thus can be used to run test methods in parallel.
 *
 * @see ParSeqUnitTestHelper
 * @see BaseEngineJunitJupiterTest
 */
public class BaseIsolatedEngineJUnitJupiterTest extends AbstractBaseEngineTest {

  private volatile boolean _setUpCalled = false;
  private volatile boolean _tearDownCalled = false;

  @BeforeEach
  public void setUpBaseEngineTest() throws Exception {
    if (!_setUpCalled) {
      _setUpCalled = true;
      _tearDownCalled = false;
      getParSeqUnitTestHelper().setUp();
    }
  }

  @AfterEach
  public void tearDownBaseEngineTest() throws Exception {
    if (!_tearDownCalled) {
      _setUpCalled = false;
      _tearDownCalled = true;
      if (getEngine() != null) {
        getParSeqUnitTestHelper().tearDown();
      }  else {
        throw new RuntimeException("Tried to shut down Engine but it either has not even been created or has "
            + "already been shut down. Please make sure you are not running unit tests in parallel. If you need to "
            + "run unit tests in parallel, then use BaseEngineJunitJupiterTest instead, in "  + this.getClass().getName());
      }
    }
  }

  protected void customizeEngine(EngineBuilder engineBuilder) {
  }

}
