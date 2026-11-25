/*
 * Copyright 2026 LinkedIn, Inc
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

package com.linkedin.parseq;

import com.linkedin.parseq.internal.ArgumentUtil;
import java.util.concurrent.TimeUnit;


/**
 * An immutable snapshot of a plan that has been started by an {@link Engine} and has not completed yet.
 * Returned by {@link Engine#getActivePlans()}.
 */
public final class ActivePlanInfo {
  private final long _planId;
  private final String _planClass;
  private final String _rootTaskName;
  private final long _startNanos;
  private final long _snapshotNanos;

  ActivePlanInfo(long planId, String planClass, String rootTaskName, long startNanos, long snapshotNanos) {
    _planId = planId;
    _planClass = planClass;
    _rootTaskName = rootTaskName;
    _startNanos = startNanos;
    _snapshotNanos = snapshotNanos;
  }

  ActivePlanInfo at(long snapshotNanos) {
    return new ActivePlanInfo(_planId, _planClass, _rootTaskName, _startNanos, snapshotNanos);
  }

  /** Returns the plan id, which is also the id used in the plan's trace. */
  public long getPlanId() {
    return _planId;
  }

  /** Returns the plan class the plan was started with. */
  public String getPlanClass() {
    return _planClass;
  }

  /** Returns the name of the plan's root task as it was when the plan started. */
  public String getRootTaskName() {
    return _rootTaskName;
  }

  /** Returns how long the plan had been running when the snapshot was taken. */
  public long getAge(TimeUnit unit) {
    ArgumentUtil.requireNotNull(unit, "unit");
    return unit.convert(ageNanosAt(_snapshotNanos), TimeUnit.NANOSECONDS);
  }

  long ageNanosAt(long nowNanos) {
    return Math.max(0L, nowNanos - _startNanos);
  }

  @Override
  public String toString() {
    return "ActivePlanInfo{planId=" + _planId + ", planClass=" + _planClass + ", rootTaskName=" + _rootTaskName
        + ", ageMs=" + getAge(TimeUnit.MILLISECONDS) + "}";
  }
}
