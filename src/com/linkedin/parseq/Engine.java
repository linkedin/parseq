/*
 * Copyright 2012 LinkedIn, Inc
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

import java.util.concurrent.TimeUnit;


/**
 * An Engine can run a {@link Task}. Use {@link EngineBuilder} to
 * create Engine instances.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public interface Engine {
  public static final String LOGGER_BASE = Engine.class.getName();

  public Object getProperty(String key);

  /**
   * Runs the given task with its own context. Use {@code Tasks.seq} and
   * {@code Tasks.par} to create and run composite tasks.
   *
   * @param task the task to run
   */
  public void run(final Task<?> task);

  /**
   * Runs the given task with its own context. Use {@code Tasks.seq} and
   * {@code Tasks.par} to create and run composite tasks.
   *
   * @param task the task to run
   */
  public void run(final Task<?> task, final String planClass);

  /**
   * If the engine is currently running, this method will initiate an orderly
   * shutdown. No new tasks will be accepted, but already running tasks will be
   * allowed to finish. Use {@link #awaitTermination(int, java.util.concurrent.TimeUnit)}
   * to wait for the engine to shutdown.
   * <p>
   * If the engine is already shutting down or stopped this method will have
   * no effect.
   */
  public void shutdown();

  /**
   * Returns {@code true} if engine shutdown has been started or if the engine
   * is terminated. Use {@link #isTerminated()} to determine if the engine is
   * actually stopped and {@link #awaitTermination(int, java.util.concurrent.TimeUnit)}
   * to wait for the engine to stop.
   *
   * @return {@code true} if the engine has started shutting down or if it has
   *         finished shutting down.
   */
  public boolean isShutdown();

  /**
   * Returns {@code true} if the engine has completely stopped. Use
   * {@link #awaitTermination(int, java.util.concurrent.TimeUnit)} to wait for
   * the engine to terminate. Use {@link #shutdown()} to start engine shutdown.
   *
   * @return {@code true} if the engine has completed stopped.
   */
  public boolean isTerminated();

  /**
   * Waits for the engine to stop. Use {@link #shutdown()} to initiate
   * shutdown.
   *
   * @param time the amount of time to wait
   * @param unit the unit for the time to wait
   * @return {@code true} if shutdown completed within the specified time or
   *         {@code false} if not.
   * @throws InterruptedException if this thread is interrupted while waiting
   *         for the engine to stop.
   */
  public boolean awaitTermination(final int time, final TimeUnit unit) throws InterruptedException;

}
