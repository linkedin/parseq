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

package com.linkedin.parseq.zk.recipes;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.zk.client.PlanLocal;
import com.linkedin.parseq.zk.client.ZKClient;
import com.linkedin.parseq.zk.client.ZKUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An inter-process mutual exclusive lock based on Zookeeper recipes:
 * http://zookeeper.apache.org/doc/trunk/recipes.html#sc_recipes_Locks
 *
 * @author Ang Xu
 */
public class ZKLock implements Synchronizable {
  private static final Logger LOG = LoggerFactory.getLogger(ZKLock.class);
  private static final String LOCK_INTERNAL_KEY = "lockInternal";

  private final String _lockPath;
  //TODO: add ACL control
  private final List<ACL> _acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
  private final ZKClient _zkClient;

  public ZKLock(String lockPath, ZKClient zkClient) {
    _lockPath = lockPath;
    _zkClient = zkClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Task<T> synchronize(Task<T> task, long deadline) {
    return PlanLocal.get(getPlanLocalKey(), LockInternal.class)
        .flatMap(lockInternal -> {
          if (lockInternal != null) {
            // we already acquire the lock, add count only.
            lockInternal._lockCount++;
            return Task.value(lockInternal._lockNode);
          } else {
            // try acquire.
            return acquire(deadline);
          }
        })
        /* run the given task with toTry() */
        .flatMap(unused -> task).toTry()
        /* release the lock and unwind the result */
        .flatMap(result -> release().andThen(unwind(result)));
  }

  /**
   * Unwinds the result from {@link Task#toTry()}.
   *
   * @return task with the raw result.
   */
  private <T> Task<T> unwind(Try<T> result) {
    if (result.isFailed()) {
      return Task.failure(result.getError());
    } else {
      return Task.value(result.get());
    }
  }

  /**
   * Try to acquire the lock within the given deadline.
   */
  private Task<String> acquire(long deadline) {
    final String uuid = UUID.randomUUID().toString();

    return
        /* Step 1: create znode with a pathname of "_locknode_/guid-lock-"
         * and the sequence and ephemeral flags set. */
        safeCreateLockNode(uuid, deadline, false)
            .onFailure(e -> _zkClient.deleteNodeHasUUID(_lockPath, uuid))
            .flatMap(lockNode -> tryAcquire(lockNode, deadline)
                .withTimeout(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .onFailure(e -> _zkClient.deleteNode(lockNode)));
  }

  /**
   * Try to release the lock.
   */
  private Task<Void> release() {
    return PlanLocal.get(getPlanLocalKey(), LockInternal.class).flatMap(lockInternal -> {
      // should never be null.
      if (lockInternal == null) {
        LOG.error("LockInternal is null when releasing lock: ", _lockPath);
      } else {
        lockInternal._lockCount--;
        if (lockInternal._lockCount == 0) {

          return PlanLocal.remove(getPlanLocalKey())
              .flatMap(unused ->
                  _zkClient.delete(lockInternal._lockNode, -1)
                      .recover(e -> {
                        _zkClient.deleteNode(lockInternal._lockNode);
                        return null;
                      })
              );
        }
      }
      return Task.value(null);
    });
  }

  private Task<String> tryAcquire(String lockNode, long deadline) {
    return
        /* Step 2: Call getChildren( ) on the lock node without setting the watch flag */
        _zkClient.getChildren(_lockPath)
            .flatMap(children -> {
              children = children.stream().map(child -> ZKUtil.normalizeZKPath(_lockPath, child))
                  .collect(Collectors.toList());
              String node = ZKUtil.findNodeWithNextLowestSN(children, lockNode);
              if (node.equals(lockNode)) {
                /* Step 3: If the pathname created in step 1 has the lowest sequence number suffix,
                 * the client has the lock and the client exits the protocol. */
                return PlanLocal.put(getPlanLocalKey(), new LockInternal(lockNode)).map(unused -> lockNode);
              } else {
                /* Step 4: The client calls exists( ) with the watch flag set on the path
                 * in the lock directory with the next lowest sequence number. */
                Task<Void> wait = Task.async(context -> {
                  AtomicBoolean committed = new AtomicBoolean(false);
                  SettablePromise<Void> promise = Promises.settable();
                  Task<Optional<Stat>> exists = _zkClient.exists(node).withWatcher(watchEvent -> {
                    /* Step 5: Otherwise, wait for a notification for the pathname from the
                     * previous step before going to step 2. */
                    if (committed.compareAndSet(false, true)) {
                      promise.done(null);
                    }
                  }).andThen(stat -> {
                    /* Step 5: if exists( ) returns false, go to step 2. */
                    if (!stat.isPresent() && committed.compareAndSet(false, true)) {
                      promise.done(null);
                    }
                  });
                  context.run(exists);
                  return promise;
                });
                /* got to step 2. */
                return wait.flatMap(unused -> tryAcquire(lockNode, deadline));
              }
            })
            .recoverWith(e -> retry(e, deadline, tryAcquire(lockNode, deadline)));
  }


  private Task<String> safeCreateLockNode(String uuid, long deadline, boolean retry) {
    if (retry) {
      /* If this is a retry, check for a node containing the guid used in the path name. Per:
       * http://zookeeper.apache.org/doc/trunk/recipes.html#sc_recipes_GuidNote */
      return _zkClient.getChildren(_lockPath)
          .map(children -> ZKUtil.findNodeWithUUID(children, uuid))
          .flatMap(node -> {
            if (node == null) {
              return createLockNode(uuid);
            } else {
              return Task.value(node);
            }
          })
          .recoverWith(e -> retry(e, deadline, safeCreateLockNode(uuid, deadline, true)));
    } else {
      return createLockNode(uuid).recoverWith(e -> retry(e, deadline, safeCreateLockNode(uuid, deadline, true)));
    }
  }

  private <T> Task<T> retry(Throwable e, long deadline, Task<T> retryTask) {
    if (System.currentTimeMillis() >= deadline) {
      return Task.failure(
          new TimeoutException("Failed to create lock node under path:" + _lockPath + " before deadline passes"));
    } else if (ZKUtil.isRecoverable(e)) {
      return _zkClient.waitFor(KeeperState.SyncConnected, deadline).flatMap(unused -> retryTask);
    } else {
      return Task.failure(e);
    }
  }

  private Task<String> createLockNode(String uuid) {
    return _zkClient.ensurePathExists(_lockPath)
        .flatMap(path -> _zkClient.create(
            ZKUtil.normalizeZKPath(path, uuid + "-lock-"), /* path to the lock node */
            LockUtil.getLockData(),      /* data */
            _acls, /* ACLs */
            CreateMode.EPHEMERAL_SEQUENTIAL /* CreateMode */));
  }

  private String getPlanLocalKey() {
    return LOCK_INTERNAL_KEY + _lockPath;
  }

  private static class LockInternal {
    private final String _lockNode;
    private long _lockCount;

    public LockInternal(String lockNode) {
      _lockNode = lockNode;
      _lockCount = 1;
    }
  }
}

