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

package com.linkedin.parseq.zk.client;


import com.linkedin.d2.discovery.stores.zk.ZKConnection;
import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.zk.server.ZKServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;


/**
 * @author Ang Xu
 */
public class TestZKClient extends BaseEngineTest {

  private ZKServer _zkServer;
  private ZKClient _zkClient;
  private ZKConnection _zooKeeper;

  @BeforeMethod
  public void setUp()
      throws Exception {
    super.setUp();
    try {
      _zkServer = new ZKServer();
      _zkServer.startup();
      _zkClient = new ZKClient("localhost:" + _zkServer.getPort(), 10000, getEngine());
      _zkClient.start().await(10, TimeUnit.SECONDS);
      _zooKeeper = new ZKConnection("localhost:" + _zkServer.getPort(), 10000);
      _zooKeeper.start();
    } catch (IOException e) {
      fail("Failed to setup zkServer or zkClient", e);
    }
  }

  @AfterMethod
  public void tearDown()
      throws Exception {
    super.tearDown();
    _zkClient.shutdown();
    _zooKeeper.shutdown();
    _zkServer.shutdown();
  }

  @Test
  public void testCreate()
      throws InterruptedException, KeeperException {
    final String path = "/testCreate";
    final byte[] data = "hello world".getBytes();

    Task<String> create = _zkClient.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    runAndWait("create", create);

    Assert.assertEquals(create.get(), path);
    Assert.assertNotNull(_zooKeeper.getZooKeeper().exists(path, false));
  }

  @Test
  public void testName()
      throws Exception {

  }

  @Test
  public void testGetData() {
    final String path = "/testGetData";
    final byte[] data = "hello world2".getBytes();

    Task<String> create = _zkClient.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    runAndWait("create", create);

    Task<ZKData> getData = _zkClient.getData(path);
    runAndWait("getData", getData);

    byte[] dataResult = getData.get().getBytes();
    Stat statResult = getData.get().getStat();

    Assert.assertNotNull(dataResult);
    Assert.assertNotNull(statResult);
    Assert.assertEquals(dataResult, data);
    Assert.assertEquals(statResult.getVersion(), 0);
    Assert.assertEquals(statResult.getDataLength(), data.length);
  }

  @Test
  public void testSetData() {
    final String path = "/testSetData";
    final byte[] data1 = "hello world".getBytes();
    final byte[] data2 = "hello world3".getBytes();

    Task<String> create = _zkClient.create(path, data1, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    runAndWait("create", create);

    Task<ZKData> getData1 = _zkClient.getData(path);
    Task<Stat> getAndSetData = getData1.flatMap(results -> _zkClient.setData(path, data2,
        results.getStat().getVersion()));

    runAndWait("getAndSetData", getAndSetData);

    // before #setData
    Assert.assertEquals(getData1.get().getBytes(), data1);
    Assert.assertEquals(getData1.get().getStat().getVersion(), 0);
    // after #setData
    Assert.assertEquals(getAndSetData.get().getVersion(), 1);
    Assert.assertEquals(getAndSetData.get().getDataLength(), data2.length);
  }

  @Test
  public void testEnsurePathExists()
      throws InterruptedException, KeeperException {
    final String path = "/testEnsurePathExists/1/2/3/4/5";

    Task<String> ensure = _zkClient.ensurePathExists(path);
    runAndWait("ensure", ensure);

    Assert.assertEquals(ensure.get(), path);
    Assert.assertNotNull(_zooKeeper.getZooKeeper().exists(path, false));
  }

  @Test
  public void testGetChildren()
      throws InterruptedException, KeeperException {
    final String parent = "/testGetChildren";
    final List<String> children = Arrays.asList(new String[]{"foo", "bar", "baz", "qux"});

    List<Task<String>> createChildrenTasks = new ArrayList<>(children.size());
    for (String child : children) {
      createChildrenTasks.add(_zkClient.create(parent + "/" + child, null,
          ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
    }
    Task<List<String>> prepare = _zkClient.ensurePathExists(parent).andThen(Tasks.par(createChildrenTasks));
    Task<List<String>> getChildren = _zkClient.getChildren(parent);

    runAndWait("getChildren", prepare.andThen(getChildren));

    List<String> getChildren1 = getChildren.get();
    List<String> getChildren2 = _zooKeeper.getZooKeeper().getChildren(parent, false);
    Assert.assertTrue(children.containsAll(getChildren1) && getChildren1.containsAll(children));
    Assert.assertTrue(getChildren2.containsAll(getChildren1) && getChildren1.containsAll(getChildren2));
  }

  @Test
  public void testGetChildrenWithWatcher()
      throws InterruptedException, KeeperException {
    final String parent = "/testGetChildrenWithWatcher";
    final List<String> children = Arrays.asList(new String[]{ "foo", "bar", "baz", "qux" });
    final CountDownLatch latch = new CountDownLatch(1);

    List<Task<String>> createChildrenTasks = new ArrayList<>(children.size());
    for (String child : children) {
      createChildrenTasks.add(_zkClient.create(parent + "/" + child, null,
          ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
    }
    Task<List<String>> prepare = _zkClient.ensurePathExists(parent).andThen(Tasks.par(createChildrenTasks));
    Task<List<String>> getChildren = _zkClient.getChildren(parent).withWatcher(watchEvent -> latch.countDown());

    runAndWait("getChildren2", prepare.andThen(getChildren));

    Assert.assertTrue(children.containsAll(getChildren.get()) && getChildren.get().containsAll(children));
    /* make sure the watcher is not fired */
    Assert.assertEquals(latch.getCount(), 1);
    /* delete a child */
    _zooKeeper.getZooKeeper().delete(parent + "/foo", -1);
    Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
  }

  @Test
  public void testExists()
      throws InterruptedException, KeeperException {
    final String path = "/testExists";

    Task<Optional<Stat>> exist = _zkClient.exists(path);
    runAndWait("exist", _zkClient.ensurePathExists(path).andThen(exist));

    Assert.assertTrue(exist.get().isPresent());
    Assert.assertEquals(exist.get().get(), _zooKeeper.getZooKeeper().exists(path, false));
  }

  @Test
  public void testDoesNotExist()
      throws InterruptedException {
    final String path = "/pathDoesNotExist";

    Task<Optional<Stat>> exist = _zkClient.exists(path);
    runAndWait("exist", exist);

    Assert.assertFalse(exist.get().isPresent());
  }

  @Test
  public void testExistsWithWatcher()
      throws InterruptedException, KeeperException {
    final String path = "/pathToBeCreated";
    final CountDownLatch latch = new CountDownLatch(1);

    Task<Optional<Stat>> exist = _zkClient.exists(path).withWatcher(watchEvent -> latch.countDown());
    runAndWait("exist2", exist);

    Assert.assertFalse(exist.get().isPresent());
    /* make sure the watcher is not fired */
    Assert.assertEquals(latch.getCount(), 1);
    /* create the path */
    _zooKeeper.getZooKeeper().create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
  }

  @Test
  public void testWaitForConnected()
      throws InterruptedException {
    final long deadline = System.currentTimeMillis() + 10000;

    Task<Void> waitForConnected = _zkClient.waitFor(Watcher.Event.KeeperState.SyncConnected, deadline);
    getEngine().run(waitForConnected);
    waitForConnected.await(10, TimeUnit.SECONDS);

    Assert.assertNull(waitForConnected.get());
  }

  @Test
  public void testWaitForDisconnected()
      throws IOException, InterruptedException {
    final long deadline = System.currentTimeMillis() + 10000;

    Task<Void> waitForDisconnected = _zkClient.waitFor(Watcher.Event.KeeperState.Disconnected, deadline);
    run(waitForDisconnected);

    _zkServer.restart();
    waitForDisconnected.await(10, TimeUnit.SECONDS);

    Assert.assertNull(waitForDisconnected.get());
  }

}

