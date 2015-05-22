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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import com.linkedin.parseq.promise.Promises;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTaskCancellation extends BaseEngineTest {

  @Test
  public void testTaskCancellationAfterRun() throws InterruptedException {
    final AtomicReference<Throwable> cancelActionValue = new AtomicReference<>();
    final CountDownLatch runLatch = new CountDownLatch(1);
    final CountDownLatch listenerLatch = new CountDownLatch(1);
    Task<?> uncompleted = Task.async(() -> {
      runLatch.countDown();
      return Promises.settable();
    });
    uncompleted.addListener(p -> {
      if (p.isFailed() && Exceptions.isCancellation(p.getError())) {
        cancelActionValue.set(p.getError().getCause());
      }
      listenerLatch.countDown();
    } );
    getEngine().run(uncompleted);
    runLatch.await(5, TimeUnit.SECONDS);
    Exception cancelReason = new Exception();
    assertTrue(uncompleted.cancel(cancelReason));
    listenerLatch.await(5, TimeUnit.SECONDS);
    logTracingResults("TestTaskCancellation.testTaskCancellationAfterRun", uncompleted);
    assertEquals(cancelActionValue.get(), cancelReason);
  }

  @Test
  public void testTaskCancellationBeforeRun() throws InterruptedException {
    final AtomicReference<Throwable> cancelActionValue = new AtomicReference<>();
    Task<?> uncompleted = Task.async(() -> Promises.settable());
    uncompleted.addListener(p -> {
      if (p.isFailed() && Exceptions.isCancellation(p.getError())) {
        cancelActionValue.set(p.getError().getCause());
      }
    } );
    Exception cancelReason = new Exception();
    assertTrue(uncompleted.cancel(cancelReason));
    getEngine().run(uncompleted);
    uncompleted.await(5, TimeUnit.SECONDS);
    logTracingResults("TestTaskCancellation.testTaskCancellationBeforeRun", uncompleted);
    assertEquals(cancelActionValue.get(), cancelReason);
  }

  @Test
  public void testTaskCancellationAfterCompleted() throws InterruptedException {
    final AtomicReference<Throwable> cancelActionValue = new AtomicReference<>();
    Task<?> completed = Task.value(10);
    completed.addListener(p -> {
      if (p.isFailed() && Exceptions.isCancellation(p.getError())) {
        cancelActionValue.set(p.getError().getCause());
      }
    } );
    runAndWait("TestTaskCancellation.testTaskCancellationAfterCompleted", completed);
    Exception cancelReason = new Exception();
    assertFalse(completed.cancel(cancelReason));
    assertNull(cancelActionValue.get());
  }

  @Test
  public void testTaskCancellationPar() throws InterruptedException {
    final AtomicReference<Throwable> cancelActionValue = new AtomicReference<>();
    Task<Integer> completed = Task.value(10);
    final CountDownLatch runLatch = new CountDownLatch(1);
    final CountDownLatch listenerLatch = new CountDownLatch(1);
    Task<Integer> uncompleted = Task.async(() -> {
      runLatch.countDown();
      return Promises.settable();
    });
    uncompleted.addListener(p -> {
      if (p.isFailed() && Exceptions.isCancellation(p.getError())) {
        cancelActionValue.set(p.getError().getCause());
      }
      listenerLatch.countDown();
    } );
    Task<?> task =
        Task.par(completed, uncompleted).map((x, y) -> x + y).withTimeout(100, TimeUnit.MILLISECONDS).recover(e -> 0);
    runAndWait("TestTaskCancellation.testTaskCancellationPar", task);
    listenerLatch.await(5, TimeUnit.SECONDS);

    assertTrue(cancelActionValue.get() instanceof EarlyFinishException);
  }

  @Test
  public void testTaskCancellationTimeout() throws InterruptedException {
    final AtomicReference<Throwable> cancelActionValue = new AtomicReference<>();
    final CountDownLatch runLatch = new CountDownLatch(1);
    final CountDownLatch listenerLatch = new CountDownLatch(1);
    Task<Integer> uncompleted = Task.async(() -> {
      runLatch.countDown();
      return Promises.settable();
    });
    uncompleted.addListener(p -> {
      if (p.isFailed() && Exceptions.isCancellation(p.getError())) {
        cancelActionValue.set(p.getError().getCause());
      }
      listenerLatch.countDown();
    } );

    Task<?> task = uncompleted.withTimeout(10, TimeUnit.MILLISECONDS).recover(e -> 0);
    runAndWait("TestTaskCancellation.testTaskCancellationTimeout", task);
    listenerLatch.await(5, TimeUnit.SECONDS);

    assertTrue(cancelActionValue.get() instanceof EarlyFinishException);
  }

}
