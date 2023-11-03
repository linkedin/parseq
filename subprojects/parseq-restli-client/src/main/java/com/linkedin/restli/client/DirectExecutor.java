package com.linkedin.restli.client;

import java.util.concurrent.Executor;


/**
 * A simple executor implementation that executes the task immediately on the calling thread
 */
class DirectExecutor implements Executor {

  private static final DirectExecutor INSTANCE = new DirectExecutor();

  static DirectExecutor getInstance() {
    return INSTANCE;
  }

  @Override
  public void execute(Runnable command) {
    command.run();
  }
}
