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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;


/**
 * @author Ang Xu
 */
public final class LockUtil {

  private static final Charset UTF8 = Charset.forName("UTF-8");
  private static final String HOST_NAME;

  static {
    String host;
    try {
      host = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      host = "Unknown Host";
    }
    HOST_NAME = host;
  }

  private LockUtil() {
  }

  public static String getNodeName(String path) {
    int index = path.lastIndexOf('/');
    return path.substring(index+1);
  }

  /**
   * Returns lock data in a form of "hostname:threadName"
   */
  public static byte[] getLockData() {
    String str = String.format("%s:%s", HOST_NAME, Thread.currentThread().getName());
    return str.getBytes(UTF8);
  }
}

