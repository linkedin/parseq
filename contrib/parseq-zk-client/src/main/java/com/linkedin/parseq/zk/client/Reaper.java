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

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
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

  private final Engine _engine;

  public Reaper(Engine engine) {
    _engine = engine;
  }

  public void submit(Zombie zombie) {
    Task<?> reapTask = zombie.reap().onFailure(e -> {
      if (ZKUtil.isRecoverable(e)) {
        submit(zombie);
      } else {
        LOG.error("failed to delete zombie node: {}", zombie);
      }
    });

    _engine.run(reapTask);
  }

  @FunctionalInterface
  public interface Zombie {
    /**
     * Returns a {@link Task} which reaps this zombie node.
     * Note that {@link #reap()} may be called more than once for a single
     * zombie instance, therefore each time it is called, a new {@link Task}
     * must be returned.
     *
     * @return reap task
     */
    Task<Void> reap();
  }

}
