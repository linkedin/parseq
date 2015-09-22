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

package com.linkedin.parseq.example.common;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public abstract class AbstractExample {
  private volatile ScheduledExecutorService _serviceScheduler;

  public void runExample() throws Exception {
    _serviceScheduler = Executors.newScheduledThreadPool(2);
    final int numCores = Runtime.getRuntime().availableProcessors();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numCores + 1);
    final EngineBuilder builder = new EngineBuilder().setTaskExecutor(scheduler).setTimerScheduler(scheduler);
    customizeEngine(builder);
    final Engine engine = builder.build();
    try {
      doRunExample(engine);
    } finally {
      engine.shutdown();
      scheduler.shutdownNow();
      _serviceScheduler.shutdown();
      _serviceScheduler = null;
    }
  }

  protected abstract void doRunExample(Engine engine) throws Exception;

  protected void customizeEngine(EngineBuilder engineBuilder) {

  }

  protected <T> MockService<T> getService() {
    return new MockService<T>(_serviceScheduler);
  }
}
