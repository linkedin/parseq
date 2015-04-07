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

package com.linkedin.parseq.example.legacy.composite.classifier;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.legacy.composite.classifier.client.Client;
import com.linkedin.parseq.example.legacy.composite.classifier.client.impl.ClientImpl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class ClassifierDriver {
  public static void main(String[] args) throws InterruptedException {
    final long viewerId = 0;

    final Set<Long> unclassified = new HashSet<Long>();
    for (long i = 0; i < 20; i++) {
      unclassified.add(i);
    }

    final ScheduledExecutorService serviceScheduler = Executors.newSingleThreadScheduledExecutor();
    final Client restLiClient = new ClientImpl(serviceScheduler);

    final int numCores = Runtime.getRuntime().availableProcessors();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numCores + 1);
    final Engine engine = new EngineBuilder().setTaskExecutor(scheduler).setTimerScheduler(scheduler).build();

    final ClassifierPlanFactory classifier = new ClassifierPlanFactory(restLiClient);
    try {
      final Task<Map<Long, Classification>> classifications = classifier.classify(viewerId, unclassified);
      engine.run(classifications);
      classifications.await();
      System.out.println(classifications.get());

      ExampleUtil.printTracingResults(classifications);
    } finally {
      serviceScheduler.shutdownNow();
      engine.shutdown();
      scheduler.shutdownNow();
    }
  }
}
