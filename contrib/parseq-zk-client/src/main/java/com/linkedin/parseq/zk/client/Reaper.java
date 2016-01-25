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

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.r2.util.NamedThreadFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A reaper thread which is responsible for removing the zombie znode
 * from Zookeeper at background. Any failure of removal that is
 * {@link ZKUtil#isRecoverable(Throwable) recoverable} will be retried
 * as long as the Zookeeper session is still valid.
 *
 * @author Ang Xu
 */
/* package private */ class Reaper {

  private static final Logger LOG = LoggerFactory.getLogger(Reaper.class);


  private final ArrayBlockingQueue<Zombie> _queue;

  private final Engine _engine;
  private final ExecutorService _executor;

  private final ReaperRunnable _runnable = new ReaperRunnable();

  public Reaper(Engine engine) {
    this(engine, Executors.newSingleThreadExecutor(new NamedThreadFactory("ZKClient Reaper")));
  }

  public Reaper(Engine engine, ExecutorService executor) {
    _queue = new ArrayBlockingQueue<>(10);
    _engine = engine;
    _executor = executor;
    _executor.execute(_runnable);
  }

  public void submit(Zombie zombie) {
    _queue.add(zombie);
  }

  public void shutdown() {
    _runnable.shutdown();
    _executor.shutdownNow();
  }

  private class ReaperRunnable implements Runnable {

    private volatile boolean _runnable = true;

    @Override
    public void run() {
      try {
        while (_runnable) {
          Zombie zombie = _queue.take();

          Task<?> clean = zombie.cleanUp().onFailure(e -> {
            if (ZKUtil.isRecoverable(e)) {
              _queue.add(zombie);
            } else {
              LOG.error("failed to delete zombie node: {}", zombie);
            }
          });
          _engine.run(clean);
        }
      } catch (InterruptedException ex) {
        LOG.debug("Caught InterruptedException");
      }
      LOG.info("Reaper thread terminated.");
    }

    public void shutdown() {
      _runnable = false;
    }
  }

  @FunctionalInterface
  public interface Zombie {
    Task<Void> cleanUp();
  }

}
