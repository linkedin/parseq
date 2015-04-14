/* $Id$ */
package com.linkedin.parseq.example.simple;

import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.common.MockService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TimeoutWithErrorExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new TimeoutWithErrorExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<String> httpClient = getService();

    final Task<String> fetchWithTimeout =
        fetchUrl(httpClient, "http://www.google.com")
          .withTimeout(50, TimeUnit.MILLISECONDS);

    engine.run(fetchWithTimeout);

    fetchWithTimeout.await();

    System.out.println(!fetchWithTimeout.isFailed() ? "Received result: " + fetchWithTimeout.get()
        : "Error: " + fetchWithTimeout.getError());

    ExampleUtil.printTracingResults(fetchWithTimeout);
  }
}
