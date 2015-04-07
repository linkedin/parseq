/* $Id$ */
package com.linkedin.parseq.example.legacy.simple;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.common.MockService;

import java.util.concurrent.TimeUnit;

import static com.linkedin.parseq.Tasks.timeoutWithError;
import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TimeoutWithErrorExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new TimeoutWithErrorExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<String> httpClient = getService();

    final Task<String> fetch = fetchUrl(httpClient, "http://www.google.com");
    final Task<String> fetchWithTimeout = timeoutWithError(50, TimeUnit.MILLISECONDS, fetch);

    engine.run(fetchWithTimeout);

    fetchWithTimeout.await();

    System.out.println(!fetchWithTimeout.isFailed() ? "Received result: " + fetchWithTimeout.get()
        : "Error: " + fetchWithTimeout.getError());

    ExampleUtil.printTracingResults(fetchWithTimeout);
  }
}
