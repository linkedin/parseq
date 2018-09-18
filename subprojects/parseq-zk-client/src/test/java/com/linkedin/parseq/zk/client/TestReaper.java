/*
 * Copyright 2016 LinkedIn Corp.
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

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.apache.zookeeper.KeeperException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author Ang Xu
 */
public class TestReaper extends BaseEngineTest {

  private Reaper _reaper;

  @BeforeMethod
  public void setUp()
      throws Exception {
    super.setUp();
    _reaper = new Reaper(getEngine());
  }

  @AfterMethod
  public void tearDown()
      throws Exception {
    super.tearDown();
  }

  @Test
  public void testReaper()
      throws InterruptedException {
    final int COUNT = 10;
    final List<Reaper.Zombie> zombies = new ArrayList<>();
    final CountDownLatch latch = new CountDownLatch(COUNT);

    for (int i = 0; i < COUNT; ++i) {
      zombies.add(() -> Task.action("countDown", () -> latch.countDown()));
    }

    zombies.forEach(z -> _reaper.submit(z));
    Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
  }


  @Test
  public void testReaperRetry()
      throws InterruptedException {
    final int MAX_RETRY = 3;
    final CountDownLatch failure = new CountDownLatch(MAX_RETRY);
    final CountDownLatch success = new CountDownLatch(1);

    Reaper.Zombie zombie = new Reaper.Zombie() {
      private int count = 0;

      @Override
      public Task<Void> reap() {
        if (count++ < MAX_RETRY) {
          return Task.action(() -> failure.countDown())
              .andThen(Task.failure(KeeperException.create(KeeperException.Code.CONNECTIONLOSS)));
        } else {
          return Task.action(() -> success.countDown());
        }
      }
    };
    _reaper.submit(zombie);

    Assert.assertTrue(failure.await(10, TimeUnit.SECONDS));
    Assert.assertTrue(success.await(10, TimeUnit.SECONDS));
  }
}
