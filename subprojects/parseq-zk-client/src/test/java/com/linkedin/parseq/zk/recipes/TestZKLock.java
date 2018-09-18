/*
 * Copyright 2016 LinkedIn, Inc
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

package com.linkedin.parseq.zk.recipes;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.zk.client.ZKClient;
import com.linkedin.parseq.zk.client.ZKClientBuilder;
import com.linkedin.parseq.zk.client.ZKData;
import com.linkedin.parseq.zk.server.ZKServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;


/**
 * @author Ang Xu
 */
public class TestZKLock extends BaseEngineTest {

  private ZKServer _zkServer;
  private ZKClient _zkClient;
  private List<ACL> _acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;

  @BeforeMethod
  public void setUp()
      throws Exception {
    super.setUp();
    try {
      _zkServer = new ZKServer();
      _zkServer.startup();
      _zkClient = new ZKClientBuilder()
          .setConnectionString("localhost:" + _zkServer.getPort())
          .setSessionTimeout(10000)
          .setEngine(getEngine())
          .build();
      _zkClient.start().await(10, TimeUnit.SECONDS);
    } catch (IOException e) {
      fail("Failed to setup zkServer or zkClient", e);
    }
  }

  @AfterMethod
  public void tearDown()
      throws Exception {
    super.tearDown();
    _zkClient.shutdown();
    _zkServer.shutdown();
  }

  @Test
  public void testZKLock1()
      throws InterruptedException {
    testZKLock(1, 1000);
  }

  @Test
  public void testZKLock2()
      throws InterruptedException {
    testZKLock(2, 500);
  }

  @Test
  public void testZKLock3()
      throws InterruptedException {
    testZKLock(3, 333);
  }

  @Test
  public void testZKLock5()
      throws InterruptedException {
    testZKLock(5, 200);
  }

  @Test
  public void testZKLock10()
      throws InterruptedException {
    testZKLock(10, 100);
  }

  @Test
  public void testReentrant()
      throws InterruptedException {
    final long deadline = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
    final ZKLock lock = createZKLock();

    Task<Integer> synchronizedTask = lock.synchronize(lock.synchronize(Task.value(1), deadline), deadline);
    runAndWait("synchronizedTask", synchronizedTask);

    Assert.assertEquals((int) synchronizedTask.get(), 1);
  }

  @Test
  public void testReleaseAfterException()
      throws InterruptedException {
    final long deadline = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
    final String path = "/testPath";
    final String errMsg = "Boom! There is an exception! But, who cares...";
    final ZKLock lock = createZKLock(path);

    Task<Integer> synchronizedTask = lock.synchronize(Task.failure(new Exception(errMsg)), deadline);

    run(synchronizedTask);

    Assert.assertTrue(synchronizedTask.await(10, TimeUnit.SECONDS));
    Assert.assertTrue(synchronizedTask.isFailed());
    Assert.assertEquals(synchronizedTask.getError().getMessage(), errMsg);

    Task<List<String>> children = _zkClient.getChildren(path);
    runAndWait("getChildren", children);

    Assert.assertEquals(children.get().size(), 0);

    Task<ZKData> acls = _zkClient.getData(path);
    runAndWait("getData", acls);

    Assert.assertEquals(acls.get().getAclList(), _acls);
  }

  @Test
  public void testDisconnectionAndRetryFailure()
      throws InterruptedException, IOException {
    final long deadline = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
    final ZKLock lock = createZKLock();

    Task<Integer> inner = Task.value(1);
    Task<Integer> synchronizedTask = lock.synchronize(inner, deadline);
    _zkServer.shutdown(false);

    run(synchronizedTask);
    Assert.assertTrue(synchronizedTask.await(10, TimeUnit.SECONDS));

    Assert.assertTrue(synchronizedTask.isFailed());
    Assert.assertTrue(synchronizedTask.getError() instanceof TimeoutException);
    // make sure the inner task is NOT run.
    Assert.assertFalse(inner.isDone());
  }

  @Test
  public void testDisconnectionAndRetrySuccess()
      throws InterruptedException {
    final long deadline = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);
    final ZKLock lock = createZKLock();

    Task<Integer> synchronizedTask = lock.synchronize(Task.value(1), deadline);

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> future = executor.schedule(() -> {
      try {
        _zkServer.restart();
      } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    }, 20, TimeUnit.MILLISECONDS);

    runAndWait("synchronizedTask", synchronizedTask);

    Assert.assertEquals((int) synchronizedTask.get(), 1);

    future.cancel(true);
    executor.shutdownNow();
  }

  @Test
  public void testAcquireTimeout()
      throws InterruptedException {
    final long timeout = 1000;
    final ZKLock lock = createZKLock();
    final SettablePromise<Void> latch = Promises.settable();

    Task<Integer> t1 = lock.synchronize(Task.async(context -> {
      // notify t2 after t1 acquires the lock.
      latch.done(null);
      SettablePromise<Integer> promise = Promises.settable();
      context.createTimer(timeout * 2, TimeUnit.MILLISECONDS, Task.action(() -> promise.done(1)));
      return promise;
    }), System.currentTimeMillis() + 3 * timeout);

    Task<Integer> inner = Task.value(1);
    Task<Integer> t2 = Task.async(() -> latch)
        .andThen(lock.synchronize(inner, System.currentTimeMillis() + timeout));

    run(t1);
    run(t2);
    Assert.assertTrue(t1.await(30, TimeUnit.SECONDS));
    Assert.assertTrue(t2.await(30, TimeUnit.SECONDS));
    Assert.assertEquals((int) t1.get(), 1);
    Assert.assertTrue(t2.isFailed());
    Assert.assertTrue(t2.getError() instanceof TimeoutException);
    Assert.assertFalse(inner.isDone());
  }

  @Test
  public void testMultiLocks()
      throws InterruptedException {
    int loopCount = 100;
    final long deadline = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);
    MultiLocks multiLocks1 = new MultiLocks(_zkClient, _acls, "/locks/l1", "/locks/l2", "/locks/l3");
    MultiLocks multiLocks2 = new MultiLocks(_zkClient, _acls, "/locks/l3", "/locks/l1", "/locks/l2");

    final AtomicReference<Integer> sum = new AtomicReference<>(0);

    Task<Void> plan1 = loop(loopCount, () -> multiLocks1.synchronize(Task.action(() -> {
      int current = sum.get();
      // increment by one.
      sum.set(++current);
    }), deadline));

    Task<Void> plan2 = loop(loopCount, () -> multiLocks2.synchronize(Task.action(() -> {
      int current = sum.get();
      // increment by one.
      sum.set(++current);
    }), deadline));

    run(plan1);
    run(plan2);

    Assert.assertTrue(plan1.await(60, TimeUnit.SECONDS));
    plan1.get();
    Assert.assertTrue(plan2.await(60, TimeUnit.SECONDS));
    plan2.get();

    Assert.assertEquals((int) sum.get(), 2 * loopCount);
  }

  private ZKLock createZKLock()
      throws InterruptedException {
    return createZKLock("/" + new Exception().getStackTrace()[1].getMethodName());
  }

  private ZKLock createZKLock(String path)
      throws InterruptedException {
    Task<String> ensureExists = _zkClient.ensurePathExists(path);
    getEngine().run(ensureExists);
    Assert.assertTrue(ensureExists.await(10, TimeUnit.SECONDS));
    ensureExists.get();
    return new ZKLock(path, _zkClient, _acls);
  }

  private void testZKLock(int concurrency, int loopCount)
      throws InterruptedException {
    final long deadline = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS);
    final ZKLock lock = createZKLock();
    final AtomicReference<Integer> sum = new AtomicReference<>(0);
    final List<Task<Void>> plans = new ArrayList<>(concurrency);

    for (int i = 0; i < concurrency; ++i) {
      Task<Void> plan = loop(loopCount, () -> lock.synchronize(Task.action(() -> {
        int current = sum.get();
        // increment by one.
        sum.set(++current);
      }), deadline));
      plans.add(plan);
      // runs the plan.
      run(plan);
    }

    for (Task<Void> plan : plans) {
      Assert.assertTrue(plan.await(60, TimeUnit.SECONDS));
      plan.get();
    }

    Assert.assertEquals((int) sum.get(), concurrency * loopCount);
  }

  private Task<Void> loop(int times, Supplier<Task<Void>> func) {
    if (times > 0) {
      return func.get().flatMap(t -> loop(times-1, func));
    } else {
      return Task.value(null);
    }
  }
}

