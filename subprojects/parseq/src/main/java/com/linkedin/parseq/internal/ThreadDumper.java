/*
 * Copyright 2017 LinkedIn, Inc
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

package com.linkedin.parseq.internal;

import static java.lang.management.ManagementFactory.getThreadMXBean;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * This class makes a thread dump and finds deadlocks.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
class ThreadDumper {

  private static final String INDENT = "    ";
  private ThreadMXBean tmbean;
  private String findDeadlocksMethodName = "findDeadlockedThreads";
  private boolean canDumpLocks = true;

  ThreadDumper() {
    this.tmbean = getThreadMXBean();
  }

  void threadDump(StringBuilder out) {
    if (canDumpLocks && tmbean.isObjectMonitorUsageSupported() && tmbean.isSynchronizerUsageSupported()) {
      dumpThreadInfoWithLocks(out);
    } else {
      dumpThreadInfo(out);
    }
  }

  private void dumpThreadInfo(StringBuilder out) {
    long[] tids = tmbean.getAllThreadIds();
    ThreadInfo[] tinfos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
    for (ThreadInfo ti : tinfos) {
      dumpThreadInfo(ti, out);
    }
    out.append("\n");
    findDeadlock(out);
  }

  private void dumpThreadInfoWithLocks(StringBuilder out) {
    ThreadInfo[] tinfos = tmbean.dumpAllThreads(true, true);
    for (ThreadInfo ti : tinfos) {
      dumpThreadInfo(ti, out);
      LockInfo[] syncs = ti.getLockedSynchronizers();
      dumpLockInfo(syncs, out);
    }
    out.append("\n");
    findDeadlock(out);
  }

  private void dumpThreadInfo(ThreadInfo ti, StringBuilder out) {
    // dump thread information
    dumpThread(ti, out);

    // dump stack trace with locks
    StackTraceElement[] stacktrace = ti.getStackTrace();
    MonitorInfo[] monitors = ti.getLockedMonitors();
    for (int i = 0; i < stacktrace.length; i++) {
      StackTraceElement ste = stacktrace[i];
      out.append(INDENT)
        .append("at ")
        .append(ste)
        .append("\n");
      for (MonitorInfo mi : monitors) {
        if (mi.getLockedStackDepth() == i) {
          out.append(INDENT)
          .append("  - locked ")
          .append(mi)
          .append("\n");
        }
      }
    }
    out.append("\n");
  }

  private void dumpThread(ThreadInfo ti, StringBuilder out) {
    out.append("\"")
      .append(ti.getThreadName())
      .append("\"")
      .append(" Id=")
      .append(ti.getThreadId())
      .append(" ")
      .append(ti.getThreadState());
    if (ti.getLockName() != null) {
      out.append(" on lock=")
        .append(ti.getLockName());
    }
    if (ti.isSuspended()) {
      out.append(" (suspended)");
    }
    if (ti.isInNative()) {
      out.append(" (running in native)");
    }
    out.append("\n");
    if (ti.getLockOwnerName() != null) {
      out.append(INDENT)
        .append(" owned by ")
        .append(ti.getLockOwnerName())
        .append(" Id=")
        .append(ti.getLockOwnerId())
        .append("\n");
    }
  }

  private void dumpLockInfo(LockInfo[] locks, StringBuilder out) {
    if (locks.length > 0) {
      out.append(INDENT)
        .append("Locked synchronizers: count = ")
        .append(locks.length)
        .append("\n");
      for (LockInfo li : locks) {
        out.append(INDENT)
          .append("  - ")
          .append(li)
          .append("\n");
      }
      out.append("\n");
    }
  }

  private void findDeadlock(StringBuilder out) {
    long[] tids;
    if (findDeadlocksMethodName.equals("findDeadlockedThreads") && tmbean.isSynchronizerUsageSupported()) {
      tids = tmbean.findDeadlockedThreads();
      if (tids == null) {
        return;
      }

      out.append("Deadlock found:\n");
      ThreadInfo[] infos = tmbean.getThreadInfo(tids, true, true);
      for (ThreadInfo ti : infos) {
        dumpThreadInfo(ti, out);
        dumpLockInfo(ti.getLockedSynchronizers(), out);
        out.append("\n");
      }
    } else {
      tids = tmbean.findMonitorDeadlockedThreads();
      if (tids == null) {
        return;
      }
      ThreadInfo[] infos = tmbean.getThreadInfo(tids, Integer.MAX_VALUE);
      for (ThreadInfo ti : infos) {
        // dump thread information
        dumpThreadInfo(ti, out);
      }
    }
  }

}
