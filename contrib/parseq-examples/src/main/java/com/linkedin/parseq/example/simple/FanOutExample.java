/* $Id$ */
package com.linkedin.parseq.example.simple;

import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.common.MockService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class FanOutExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new FanOutExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<String> httpClient = getService();
    final Task<String> fetchBing = fetchUrl(httpClient, "http://www.bing.com");
    final Task<String> fetchYahoo = fetchUrl(httpClient, "http://www.yahoo.com");
    final Task<String> fetchGoogle = fetchUrl(httpClient, "http://www.google.com");

    final Task<?> parFetch = Task.par(fetchBing, fetchGoogle, fetchYahoo);
    engine.run(parFetch);

    parFetch.await();

    System.out.println("Bing   => " + fetchBing.get());
    System.out.println("Yahoo  => " + fetchYahoo.get());
    System.out.println("Google => " + fetchGoogle.get());

    ExampleUtil.printTracingResults(parFetch);
  }
}
