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
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class CalcellationExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new CalcellationExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<String> httpClient = getService();

    final Task<Integer> fetchAndLength =
        fetchUrl(httpClient, "http://www.google.com", 10000).withTimeout(5000, TimeUnit.MILLISECONDS)
            .recover("default", t -> "").map("length", s -> s.length()).andThen("big bang", x -> System.exit(1));

    engine.run(fetchAndLength);
    Thread.sleep(20);
    fetchAndLength.cancel(new Exception("because I said so"));

    fetchAndLength.await();

    System.out.println(!fetchAndLength.isFailed() ? "Received result: " + fetchAndLength.get()
        : "Error: " + fetchAndLength.getError());

    ExampleUtil.printTracingResults(fetchAndLength);
  }
}
