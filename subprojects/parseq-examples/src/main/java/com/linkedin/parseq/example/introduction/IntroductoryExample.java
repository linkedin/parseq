package com.linkedin.parseq.example.introduction;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.httpclient.HttpClient;
import com.ning.http.client.Response;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class IntroductoryExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new IntroductoryExample().runExample();
    HttpClient.close();
  }

  private Task<String> fetchBody(String url) {
    return HttpClient.get(url).task().map(Response::getResponseBody);
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {

    final Task<String> google = fetchBody("http://www.google.com");
    final Task<String> yahoo = fetchBody("http://www.yahoo.com");
    final Task<String> bing = fetchBody("http://www.bing.com");

//    final Task<String> plan = Task.par(google, yahoo, bing)
//        .map((g, y, b) -> "Google Page: " + g +" \n" +
//                          "Yahoo Page: " + y + "\n" +
//                          "Bing Page: " + b + "\n")
//        .andThen(System.out::println);

    final Task<Integer> sumLengths =
        google.withSideEffect(result -> {
          //throw new RuntimeException();
          return Task.failure(new RuntimeException());
        }).map(String::length);

    engine.run(sumLengths);

    sumLengths.await();
    System.out.println(sumLengths.get());

    ExampleUtil.printTracingResults(sumLengths);
  }
}
