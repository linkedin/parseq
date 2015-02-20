/* $Id$ */
package com.linkedin.parseq.example.legacy.simple;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;

import static com.linkedin.parseq.Tasks.*;
import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class FanInExample extends AbstractExample
{
  public static void main(String[] args) throws Exception
  {
    new FanInExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception
  {
    final MockService<String> httpClient = getService();

    final Task<String> fetchBing = fetchUrl(httpClient, "http://www.bing.com");
    final Task<String> fetchYahoo = fetchUrl(httpClient, "http://www.yahoo.com");
    final Task<String> fetchGoogle = fetchUrl(httpClient, "http://www.google.com");

    final Task<?> printResults = action("printResults", new Runnable()
    {
      @Override
      public void run()
      {
        System.out.println("Bing   => " + fetchBing.get());
        System.out.println("Yahoo  => " + fetchYahoo.get());
        System.out.println("Google => " + fetchGoogle.get());
      }
    });

    final Task<?> parFetch = par(fetchBing, fetchGoogle, fetchYahoo);
    final Task<?> fanIn = seq(parFetch, printResults);
    engine.run(fanIn);

    fanIn.await();

    printTracingResults(fanIn);
  }
}
