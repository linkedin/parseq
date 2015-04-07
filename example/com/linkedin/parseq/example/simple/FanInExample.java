/* $Id$ */
package com.linkedin.parseq.example.simple;

import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class FanInExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new FanInExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<String> httpClient = getService();

    final Task<String> fetchBing = fetchUrl(httpClient, "http://www.bing.com");
    final Task<String> fetchYahoo = fetchUrl(httpClient, "http://www.yahoo.com");
    final Task<String> fetchGoogle = fetchUrl(httpClient, "http://www.google.com");

    final Task<?> fanIn = Task.par(fetchBing, fetchGoogle, fetchYahoo).andThen((bing, google, yahoo) -> {
      System.out.println("Bing   => " + bing);
      System.out.println("Yahoo  => " + yahoo);
      System.out.println("Google => " + google);
    } );
    engine.run(fanIn);

    fanIn.await();

    printTracingResults(fanIn);
  }
}
