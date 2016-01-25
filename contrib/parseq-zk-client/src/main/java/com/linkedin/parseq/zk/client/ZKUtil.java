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

import java.util.List;
import java.util.stream.Collectors;
import org.apache.zookeeper.KeeperException;


/**
 * @author Ang Xu
 */
public final class ZKUtil {

  private ZKUtil() {}

  public static String noramlizeZKPath(String basePath, String child) {
    // normalization
    while (basePath.endsWith("/")) {
      basePath = basePath.substring(0, basePath.length() - 2);
    }
    while(child.startsWith("/")) {
      child = child.substring(1);
    }
    return basePath + "/" + child;
  }

  /**
   * Decides whether the given {@link Throwable throwable} is recoverable or not.
   * Recoverable exceptions are defined as such:
   * http://wiki.apache.org/hadoop/ZooKeeper/ErrorHandling
   *
   * @param t the {@link Throwable}
   * @return  {@code true} if {@code t} is recoverable and {@code false} otherwise.
   */
  public static boolean isRecoverable(Throwable t) {
    if (t instanceof KeeperException) {
      KeeperException ex = (KeeperException) t;
      if (ex.code() == KeeperException.Code.CONNECTIONLOSS ||
          ex.code() == KeeperException.Code.OPERATIONTIMEOUT) {
        return true;
      }
    }
    return false;
  }

  public static String findNodeWithUUID(List<String> children, String uuid) {
    for (String child : children) {
      if (child.startsWith(uuid)) {
        return child;
      }
    }
    return null;
  }

  /**
   * Finds node with next lowest sequence number than the given node within
   * the given list of nodes.
   *
   * @param children list of children
   * @param node the given node to compare sequence number with
   * @return the node with next lowest sequence number
   */
  public static String findNodeWithNextLowestSN(List<String> children, String node) {
    List<String> sortedChildren = children.stream()
        .sorted((String o1, String o2) ->
            Long.compare(getSequenceNumber(o1), getSequenceNumber(o2)))
        .collect(Collectors.toList());

    int index = sortedChildren.indexOf(node);
    if (index > 0) {
      return sortedChildren.get(index-1);
    } else if (index == 0) {
      return node;
    } else {
      return null;
    }
  }

  /**
   * Gets 10 digit sequence number from the given znode per:
   * http://zookeeper.apache.org/doc/trunk/zookeeperProgrammers.html#Sequence+Nodes+--+Unique+Naming
   */
  private static long getSequenceNumber(String node) {
    final int length = node.length();
    try {
      String seqNumStr = node.substring(length - 10, length);
      return Long.valueOf(seqNumStr);
    } catch (IndexOutOfBoundsException | NumberFormatException ex) {
      throw new IllegalArgumentException(String.format("znode %s doesn't have sequence number", node));
    }
  }
}
