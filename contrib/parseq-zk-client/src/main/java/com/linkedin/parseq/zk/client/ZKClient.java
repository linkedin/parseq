package com.linkedin.parseq.zk.client;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.OpResult;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;


/**
 * @author Ang Xu
 */
public interface ZKClient {
  /**
   * Starts the zookeeper connection. The returned promise will be
   * resolved once the connection is established.
   *
   * @return promise
   */
  Promise<Void> start();

  /**
   * Shuts down the zookeeper connection.
   * @throws InterruptedException
   */
  void shutdown() throws InterruptedException;

  /**
   * Returns task that will create znode for the given path with the given data and acl.
   *
   * @param path  path of the znode.
   * @param data  data of the znode.
   * @param acl   acl of the znode.
   * @param createMode create mode which specifies whether the znode is persistent
   *                   or ephemeral.
   * @return task to create znode.
   */
  Task<String> create(String path, byte[] data, List<ACL> acl, CreateMode createMode);

  /**
   * Returns task that will get data of the znode of the given path.
   *
   * @param path  path of the znode.
   * @return task to get data.
   */
  WatchableTask<ZKData> getData(String path);

  /**
   * Returns task that will set the data for the node of the given path if
   * such a node exists and the given version matches the version of the node
   * (if the given version is -1, it matches any node's versions).
   *
   * @param path  path of the znode.
   * @param data  znode data to set.
   * @param version expected matching version.
   * @return task to set data.
   */
  Task<Stat> setData(String path, byte[] data, int version);

  /**
   * Returns task that will get children for the znode of the given path.
   *
   * @param path path to the znode.
   * @return task to get children.
   */
  WatchableTask<List<String>> getChildren(String path);

  /**
   * Returns task that will test whether the znode of the given path exists
   * or not. If exists, the {@link Stat} of the znode is returned.
   *
   * @param path path to the znode.
   * @return task to test existence of the znode.
   */
  WatchableTask<Optional<Stat>> exists(String path);

  /**
   * Returns task to delete znode of the given path if such a node exists
   * and the given version matches the version of the node (if the given
   * version is -1, it matches any node's versions).
   *
   * @param path path to the znode.
   * @param version expected matching version.
   * @return task to delete znode.
   */
  Task<Void> delete(String path, int version);

  /**
   * Returns task that will execute all the given {@link Op operation}s in
   * an atomic manner.
   *
   * @param ops operations to execute.
   * @param executor {@code Executor} that will be used to run the operations.
   * @return task to execute multiple operations.
   */
  Task<List<OpResult>> multi(List<Op> ops, Executor executor);

  /**
   * Returns task that will wait for the given {@link Watcher.Event.KeeperState} to fulfill.
   * The task will be failed if the underlying zookeeper session expires.
   *
   * @param state keeper state to wait for.
   * @return task that waits for a certain keeper state.
   */
  Task<Void> waitFor(Watcher.Event.KeeperState state, long deadline);

  /**
   * Checks if the path exist or not. If it doesn't exist, will create the znode.
   * @param path path to the znode.
   * @return path to the znode
   */
  Task<String> ensurePathExists(String path);

  /**
   * Deletes a znode in background.
   * @param path path to the znode to delete
   */
  void deleteNode(String path);

  /**
   * Deletes children of the given path whose name matches the given uuid.
   * @param path parents znode path
   * @param uuid uuid of the znode to delete
   */
  void deleteNodeHasUUID(String path, String uuid);
}
