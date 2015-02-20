/* $Id$ */
package com.linkedin.parseq.example.collections;

import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.collection.ParSeqCollections;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.common.MockService;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class ParFilterExample extends AbstractExample
{
  public static void main(String[] args) throws Exception
  {
    new ParFilterExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception
  {
    final MockService<String> httpClient = getService();
    List<String> urls = Arrays.asList("http://www.linkedin.com", "http://www.google.com", "http://www.twitter.com");

    List<Task<String>> fetchSizes = fetchList(httpClient, urls);

    Task<String> find =
        ParSeqCollections.fromTasks(fetchSizes)
          .filter(s -> !s.contains("google"))
          .flatMap(z -> ParSeqCollections.fromTasks(fetchList(httpClient, urls))
                .filter(s -> s.contains("twitter")))
          .find(s -> s.contains("twitter"));

    engine.run(find);

    find.await();

    System.out.println("found: " + find.get());

    ExampleUtil.printTracingResults(find);
  }

  private List<Task<String>> fetchList(final MockService<String> httpClient, List<String> urls) {
    List<Task<String>> fetchSizes =
      urls.stream()
        .map(url ->
              fetchUrl(httpClient, url)
                 .withTimeout(200, TimeUnit.MILLISECONDS)
                 .recover("default", t -> ""))
        .collect(Collectors.toList());
    return fetchSizes;
  }

}
