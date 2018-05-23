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

import java.util.List;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;


/**
 * @author Ang Xu
 */
public class ZKData {
  private final String _path;
  private final byte[] _bytes;
  private final Stat _stat;
  private final List<ACL> _aclList;

  public ZKData(String path, byte[] bytes, Stat stat, List<ACL> acls) {
    _path = path;
    _bytes = bytes;
    _stat = stat;
    _aclList = acls;
  }

  public String getPath() {
    return _path;
  }

  public byte[] getBytes() {
    return _bytes;
  }

  public Stat getStat() {
    return _stat;
  }

  public List<ACL> getAclList() {
    return _aclList;
  }
}
