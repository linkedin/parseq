/* $Id$ */
package com.linkedin.parseq.example.simple;

import static com.linkedin.parseq.example.common.ExampleUtil.fetch404Url;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.common.MockService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class ErrorRecoveryExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new ErrorRecoveryExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<String> httpClient = getService();

    final Task<Integer> fetchAndLength =
        fetch404Url(httpClient, "http://www.google.com")
          .recover("default", t -> "")
          .map("length", s -> s.length());

    engine.run(fetchAndLength);

    fetchAndLength.await();

    System.out.println("Response length: " + fetchAndLength.get());

    ExampleUtil.printTracingResults(fetchAndLength);
  }
}
