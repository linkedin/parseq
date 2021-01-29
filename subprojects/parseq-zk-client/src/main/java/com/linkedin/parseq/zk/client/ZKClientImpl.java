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

import com.linkedin.parseq.Context;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A thin wrapper around vanilla zookeeper client to facilitate the usage of
 * <a href="www.github.com/linkedin/parseq">Parseq</a>.
 *
 * @author Ang Xu
 */
class ZKClientImpl implements ZKClient {

  private static final Logger LOG = LoggerFactory.getLogger(ZKClientImpl.class);

  private volatile ZooKeeper _zkClient;
  private volatile Reaper _reaper;

  private final String _connectionString;
  private final int _sessionTimeout;
  private final Engine _engine;

  private final Watcher _defaultWatcher = new DefaultWatcher();
  private final Object _mutex = new Object();
  private final StateListener _listener = new StateListener();
  private KeeperState _currentState = null;

  /**
   * Constructs a {@link ZKClientImpl} with the given parameters.
   *
   * @param connectionString comma separated host:port pairs, each corresponding to
   *                         a zk server.
   * @param sessionTimeout   session timeout in milliseconds.
   * @param engine           a parseq {@link Engine} to schedule background tasks.
   */
  public ZKClientImpl(String connectionString, int sessionTimeout, Engine engine) {
    _connectionString = connectionString;
    _sessionTimeout = sessionTimeout;
    _engine = engine;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Promise<Void> start() {
    AtomicBoolean committed = new AtomicBoolean(false);
    SettablePromise<Void> promise = Promises.settable();
    _listener.subscribe(KeeperState.SyncConnected,
        (Promise p) -> {
          if (committed.compareAndSet(false, true)) {
            if (p.isFailed()) {
              promise.fail(p.getError());
            } else {
              promise.done(null);
            }
          }
        }
    );

    try {
      _zkClient = new ZooKeeper(_connectionString, _sessionTimeout, _defaultWatcher);
      _reaper = new Reaper(_engine);
    } catch (IOException ex) {
      if (committed.compareAndSet(false, true)) {
        promise.fail(ex);
      }
    }
    return promise;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown() throws InterruptedException {
    if (_zkClient != null) {
      _zkClient.close();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<String> create(String path, byte[] data, List<ACL> acl, CreateMode createMode) {
    return Task.async("zkCreate: " + path, () -> {
      SettablePromise<String> promise = Promises.settable();
      _zkClient.create(path, data, acl, createMode,
          (int rc, String p, Object ctx, String name) -> {
            KeeperException.Code code = KeeperException.Code.get(rc);
            switch (code) {
              case OK:
                promise.done(name);
                break;
              default:
                promise.fail(KeeperException.create(code, p));
            }
          }, null);
      return promise;
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WatchableTask<ZKData> getData(String path) {
    return new WatchableTask<ZKData>("zkGetData: " + path) {
      @Override
      protected Promise<? extends ZKData> run(Context context)
          throws Throwable {
        SettablePromise<ZKData> promise = Promises.settable();

        _zkClient.getData(path, _watcher,
            (int rc, String p, Object ctx, byte[] data, Stat stat) -> {
              KeeperException.Code code = KeeperException.Code.get(rc);
              switch (code) {
                case OK:
                  _zkClient.getACL(path, stat,
                      (int rc1, String p1, Object ctx1, List<ACL> acls, Stat stat1) -> {
                        KeeperException.Code code1 = KeeperException.Code.get(rc1);
                        switch (code1) {
                          case OK:
                            promise.done(new ZKData(p, data, stat, acls));
                            break;
                          default:
                            promise.fail(KeeperException.create(code1, p1));
                        }
                      }, null);
                  break;
                default:
                  promise.fail(KeeperException.create(code, p));
              }
            }, null);
        return promise;
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<Stat> setData(String path, byte[] data, int version) {
    return Task.async("zkSetData: " + path, () -> {
      SettablePromise<Stat> promise = Promises.settable();
      _zkClient.setData(path, data, version,
          (int rc, String p, Object ctx, Stat stat) -> {
            KeeperException.Code code = KeeperException.Code.get(rc);
            switch (code) {
              case OK:
                promise.done(stat);
                break;
              default:
                promise.fail(KeeperException.create(code, p));
            }
          }, null);
      return promise;
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WatchableTask<List<String>> getChildren(String path) {
    return new WatchableTask<List<String>>("zkGetChildren: " + path) {
      @Override
      protected Promise run(Context context) throws Throwable {
        SettablePromise<List<String>> promise = Promises.settable();
        _zkClient.getChildren(path, _watcher, (int rc, String p, Object ctx, List<String> children) -> {
          KeeperException.Code code = KeeperException.Code.get(rc);
          switch (code) {
            case OK:
              promise.done(children);
              break;
            default:
              promise.fail(KeeperException.create(code, p));
          }
        }, null);
        return promise;
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WatchableTask<Optional<Stat>> exists(String path) {
    return new WatchableTask<Optional<Stat>>("zkExists: " + path) {
      @Override
      protected Promise run(Context context) throws Throwable {
        SettablePromise<Optional<Stat>> promise = Promises.settable();
        _zkClient.exists(path, _watcher, (int rc, String p, Object ctx, Stat stat) -> {
          KeeperException.Code code = KeeperException.Code.get(rc);
          switch (code) {
            case OK:
              promise.done(Optional.of(stat));
              break;
            case NONODE:
              promise.done(Optional.empty());
              break;
            default:
              promise.fail(KeeperException.create(code, p));
          }
        }, null);
        return promise;
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<Void> delete(String path, int version) {
    return Task.async("zkDelete: " + path, () -> {
      SettablePromise<Void> promise = Promises.settable();
      _zkClient.delete(path, version, (int rc, String p, Object ctx) -> {
        KeeperException.Code code = KeeperException.Code.get(rc);
        switch (code) {
          case OK:
            promise.done(null);
            break;
          default:
            promise.fail(KeeperException.create(code, p));
        }
      }, null);
      return promise;
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<List<OpResult>> multi(List<Op> ops, Executor executor) {
    return Task.runInExecutor(() -> _zkClient.multi(ops), executor);
  }

  /**
   * Returns task that will wait for the given {@link KeeperState} to fulfill.
   * The task will be failed if the underlying zookeeper session expires.
   *
   * @param state keeper state to wait for.
   * @return task that waits for a certain keeper state.
   */
  public Task<Void> waitFor(KeeperState state) {
    return Task.async("waitFor " + state.toString(), () -> {
      SettablePromise<Void> promise = Promises.settable();
      synchronized (_mutex) {
        if (_currentState == state) {
          return Promises.VOID;
        } else {
          _listener.subscribe(state, (p) -> {
            if (p.isFailed()) {
              promise.fail(p.getError());
            } else {
              promise.done(null);
            }
          });
        }
      }
      return promise;
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<Void> waitFor(KeeperState state, long deadline) {
    return waitFor(state).withTimeout(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<String> ensurePathExists(String path) {
    return exists(path).flatMap(stat -> {
      if (!stat.isPresent()) {
        Task<String> createIfAbsent = create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
            .recoverWith("recover from NodeExistsException", e -> {
              if (e instanceof KeeperException.NodeExistsException) {
                return Task.value(path);
              } else {
                return Task.failure(e);
              }
            });
        String parent = path.substring(0, path.lastIndexOf('/'));
        if (parent.isEmpty()) { // parent is root
          return createIfAbsent;
        } else {
          return ensurePathExists(parent).flatMap(unused -> createIfAbsent);
        }
      } else {
        return Task.value(path);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteNode(String node) {
    _reaper.submit(() -> delete(node, -1));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteNodeHasUUID(String path, String uuid) {
    _reaper.submit(() -> getChildren(path).map(children -> ZKUtil.findNodeWithUUID(children, uuid)).flatMap(node -> {
      if (node != null) {
        return delete(node, -1);
      } else {
        return Task.value(null);
      }
    }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addAuthInfo(String scheme, byte[] auth) {
    _zkClient.addAuthInfo(scheme, auth);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Task<Stat> setACL(String path, List<ACL> acls, int version) {
    return Task.async("zkSetACL: " + path, () -> {
      SettablePromise<Stat> promise = Promises.settable();
      _zkClient.setACL(path, acls, version,
          (int rc, String p, Object ctx, Stat stat) -> {
            KeeperException.Code code = KeeperException.Code.get(rc);
            switch (code) {
              case OK:
                promise.done(stat);
                break;
              default:
                promise.fail(KeeperException.create(code, p));
            }
          }, null);
      return promise;
    });
  }

  private class DefaultWatcher implements Watcher {
    @Override
    public void process(WatchedEvent watchedEvent) {
      if (watchedEvent.getType() == Event.EventType.None) {
        Event.KeeperState state = watchedEvent.getState();
        switch (state) {
          case Disconnected:
          case SyncConnected:
          case AuthFailed:
          case ConnectedReadOnly:
          case SaslAuthenticated:
            synchronized (_mutex) {
              _currentState = state;
              _listener.done(state);
            }
            break;
          case Expired:
            synchronized (_mutex) {
              _currentState = state;
              for (KeeperState ks : KeeperState.values()) {
                if (ks == KeeperState.Expired) {
                  _listener.done(KeeperState.Expired);
                } else {
                  _listener.fail(ks, KeeperException.create(KeeperException.Code.SESSIONEXPIRED));
                }
              }
            }
            break;
          default:
            LOG.warn("Received unknown state {} for session 0x{}", state, Long.toHexString(_zkClient.getSessionId()));
        }
      }
    }
  }

  /**
   * A collection of listeners listen to Zookeeper {@link KeeperState state}.
   */
  private static class StateListener {
    private final Map<KeeperState, SettablePromise<Void>> _listeners;

    public StateListener() {
      _listeners = new EnumMap<>(KeeperState.class);
      for (KeeperState state : KeeperState.values()) {
        _listeners.put(state, Promises.settable());
      }
    }

    public void done(KeeperState state) {
      SettablePromise promise = _listeners.put(state, Promises.settable());
      promise.done(null);
    }

    public void fail(KeeperState state, Exception e) {
      SettablePromise promise = _listeners.put(state, Promises.settable());
      promise.fail(e);
    }

    public void subscribe(KeeperState state, PromiseListener listener) {
      _listeners.get(state).addListener(listener);
    }

  }
}
