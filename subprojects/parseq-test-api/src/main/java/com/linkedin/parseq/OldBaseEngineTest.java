/*
 * Copyright 2021 LinkedIn, Inc
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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;


abstract class OldBaseEngineTest extends AbstractBaseEngineTest {
  
  /**
   * This method is left for backwards compatibility purpose.
   * TODO in next major version this method should be removed
   * @deprecated
   */
  @Deprecated
  @BeforeMethod
  public void setUpBaseEngineTest() throws Exception {
  }

  /**
   * This method is left for backwards compatibility purpose.
   * TODO in next major version this method should be removed
   * @deprecated
   */
  @Deprecated
  @BeforeMethod
  public void setUp() throws Exception {
  }

  /**
   * This method is left for backwards compatibility purpose.
   * TODO in next major version this method should be removed
   * @deprecated
   */
  @Deprecated
  @AfterMethod
  public void tearDown() throws Exception {
  }

  /**
   * This method is left for backwards compatibility purpose.
   * TODO in next major version this method should be removed
   * @deprecated
   */
  @Deprecated
  @AfterMethod
  public void tearDownBaseEngineTest() throws Exception {
  }
}
