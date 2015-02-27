/* $Id$ */
package com.linkedin.parseq.example.collections;

import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;

import java.util.Arrays;
import java.util.List;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.collection.ParSeqCollection;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.common.MockService;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class SyncCollectionFlatMapExample extends AbstractExample
{
  public static void main(String[] args) throws Exception
  {
    new SyncCollectionFlatMapExample().runExample();
  }

  static final List<String> urls = Arrays.asList("http://www.linkedin.com", "http://www.google.com", "http://www.twitter.com");
  static final List<String> paths = Arrays.asList("/p1", "/p2");

  @Override
  protected void doRunExample(final Engine engine) throws Exception
  {
    final MockService<String> httpClient = getService();

    Task<String> task = ParSeqCollection.fromValues(urls)
      .flatMap(base -> ParSeqCollection.fromValues(paths)
          .map(path -> base + path)
          .mapTask(url -> fetchUrl(httpClient, url)))
      .reduce((a, b) -> a + "\n" + b)
      .recover(e -> "error: " + e.toString())
      .andThen(System.out::println);

    engine.run(task);

    task.await();

    ExampleUtil.printTracingResults(task);
  }
}
