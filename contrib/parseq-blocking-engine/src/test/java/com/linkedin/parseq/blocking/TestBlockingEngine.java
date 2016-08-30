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

package com.linkedin.parseq.blocking;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestBlockingEngine extends BaseEngineTest {

  @Test
  public void testRunWithinCapacity() throws InterruptedException {

    BlockingEngine blockingEngine = new BlockingEngine(getEngine(), 5, 5);
    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      blockingEngine.run(tasks[i]);
    }

    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));
    assertEquals(counter.get(), 10);
  }

  @Test
  public void testTryRunWithinCapacity() throws InterruptedException {

    BlockingEngine blockingEngine = new BlockingEngine(getEngine(), 5, 5);
    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      assertTrue(blockingEngine.tryRun(tasks[i]));
    }

    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));
    assertEquals(counter.get(), 10);
  }

  @Test
  public void testTimeBoundedTryRunWithinCapacity() throws InterruptedException {

    BlockingEngine blockingEngine = new BlockingEngine(getEngine(), 5, 5);
    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      assertTrue(blockingEngine.tryRun(tasks[i], 10, TimeUnit.MILLISECONDS));
    }

    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));
    assertEquals(counter.get(), 10);
  }

  @Test
  public void testBlocking() throws InterruptedException {

    BlockingEngine blockingEngine = new BlockingEngine(getEngine(), 5, 5);
    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      blockingEngine.run(tasks[i]);
    }

    //blocking one
    assertFalse(blockingEngine.tryRun(Task.action(() -> {})));

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));

    //should be unblocked
    assertTrue(blockingEngine.tryRun(Task.action(() -> {})));
  }

  @Test
  public void testTimeBoundedBlocking() throws InterruptedException {

    BlockingEngine blockingEngine = new BlockingEngine(getEngine(), 5, 5);
    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      blockingEngine.run(tasks[i]);
    }

    //time bounded blocking
    assertFalse(blockingEngine.tryRun(Task.action(() -> {}), 10, TimeUnit.MILLISECONDS));

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));

    //should be unblocked
    assertTrue(blockingEngine.tryRun(Task.action(() -> {})));
  }



}
