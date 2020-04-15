/*
 * Copyright 2017 LinkedIn, Inc
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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;


/**
 * A base class that builds an Engine with default configuration.
 * For JUnit Jupiter (JUnit5+), see {@link com.linkedin.parseq.junitjupiter.BaseEngineParJunitJupiterTest}.
 *
 * This class creates new Engine before any test method is run and shuts it down after all tests are finished.
 * It can be used to run tests in parallel.
 *
 * The difference between this class and {@link BaseEngineTest} is that {@code BaseEngineTest} creates new
 * {@code Engine} instance for every test and thus provides higher level of isolation between the tests.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BaseEngineParTest extends AbstractBaseEngineTest {

  @BeforeClass
  public void setUpBaseEngineParTest() throws Exception {
    getParSeqUnitTestHelper().setUp();
  }

  @AfterClass
  public void tearDownBaseEngineParTest() throws Exception {
    if (getEngine() != null) {
      getParSeqUnitTestHelper().tearDown();
    }  else {
      throw new RuntimeException("Tried to shut down Engine but it either has not even been created or has "
          + "already been shut down, in "  + this.getClass().getName());
    }
  }

  protected void customizeEngine(EngineBuilder engineBuilder) {
  }
}
