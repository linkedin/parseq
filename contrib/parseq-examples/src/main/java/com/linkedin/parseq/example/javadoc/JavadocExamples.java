package com.linkedin.parseq.example.javadoc;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.example.domain.Person;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.httpclient.HttpClient;
import com.ning.http.client.Response;


/**
 * This example contains code used to generate diagrams in Task's javadoc.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class JavadocExamples extends AbstractExample {
  public static void main(String[] args) throws Exception {

    for (int i = 0; i < 100; i++) {
      new JavadocExamples().runExample();
    }
    new JavadocExamples().runExample();
    HttpClient.close();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {

// map-1.svg
//      Task<String> hello = Task.value("Hello World");
//      Task<Integer> length = hello.map("length", s -> s.length());

// map-2.svg
//    Task<String> failing = Task.callable("hello", () -> {
//      return "Hello World".substring(100);
//    });
//    Task<Integer> length = failing.map("length", s -> s.length());

// flatMap-1
//    Task<URI> url = Task.value("uri", URI.create("http://linkedin.com"));
//    Task<String> homepage = url.flatMap("fetch", u -> fetch(u));

// flatMap-2
//    Task<URI> url = Task.callable("uri", () -> URI.create("not a URI"));
//    Task<String> homepage = url.flatMap("fetch", u -> fetch(u));

// withSideEffect-1
//    Task<Long> id = Task.value("id", 1223L);
//    Task<String> userName = id.flatMap("fetch", u -> fetch(u))
//        .withSideEffect("update memcache", u -> updateMemcache(u));


// shareable-1
//    final Task<Response> google = HttpClient.get("http://google.com").task();
//    final Task<Response> bing = HttpClient.get("http://bing.com").task();
//
//    final Task<?> both = Task.par(google.withTimeout(10, TimeUnit.MILLISECONDS), bing);

 // shareable-2
//  final Task<Response> google = HttpClient.get("http://google.com").task();
//  final Task<Response> bing = HttpClient.get("http://bing.com").task();
//
//  final Task<?> both = Task.par(google.shareable().withTimeout(10, TimeUnit.MILLISECONDS),
//                                bing.shareable());

// andThen-1
//    Task<String> hello = Task.value("greeting", "Hello World");
//
//    // this task will print "Hello World"
//    Task<String> sayHello = hello.andThen("say", System.out::println);

 // andThen-2
//    Task<String> failing = Task.callable("greeting", () -> {
//      return "Hello World".substring(100);
//    });
//
//    // this task will fail with java.lang.StringIndexOutOfBoundsException
//    Task<String> sayHello = failing.andThen("say", System.out::println);


//andThen-3
//    // task that processes payment
//    Task<?> processPayment = Task.callable("processPayment", () -> "");
//
//    // task that ships product
//    Task<?> shipProduct = Task.action("ship", () -> {});
//
//    // this task will ship product only if payment was
//    // successfully processed
//    Task<?> shipAfterPayment =
//        processPayment.andThen("shipProductAterPayment", shipProduct);


//recover-1
//    long id = 1234L;
//
//    // this task will fetch Person object and transform it into "<first name> <last name>"
//    // if fetching Person failed then form "Member <id>" will be return
//    Task<?> userName = fetchPerson(id)
//         .map("toSignature", p -> p.getFirstName() + " " + p.getLastName())
//         .recover(e -> "Member " + id);


//onFailure-1
//    Task<String> failing = Task.callable("greeting", () -> {
//      return "Hello World".substring(100);
//    });
//
//    // this task will print out java.lang.StringIndexOutOfBoundsException
//    // and complete with that exception as a reason for failure
//    Task<String> sayHello = failing.onFailure("printFailure", System.out::println);

//onFailure-2
//    Task<String> hello = Task.value("greeting", "Hello World");
//
//    // this task will return "Hello World"
//    Task<String> sayHello = hello.onFailure(System.out::println);

//toTry-1
//    Task<String> hello = Task.value("greeting", "Hello World");
//
//    // this task will complete with Success("Hello World")
//    Task<Try<String>> helloTry = hello.toTry("try");

//toTry-2
//    Task<String> failing = Task.callable("greeting", () -> {
//      return "Hello World".substring(100);
//    } );
//
//    // this task will complete successfully with Failure(java.lang.StringIndexOutOfBoundsException)
//    Task<Try<String>> failingTry = failing.toTry("try");


//transform-1
//    Task<Integer> num = Task.value("num", 10);
//
//        // this task will complete with either complete successfully
//        // with String representation of num or fail with  MyLibException
//        Task<String> text = num.transform("toString", t -> {
//          if (t.isFailed()) {
//            return Failure.of(new MyLibException(t.getError()));
//          } else {
//            return Success.of(String.valueOf(t.get()));
//          }
//        });

//recoverWith-1
//    long id = 1;
//
//    // this task will try to fetch Person from cache and
//    // if it fails for any reason it will attempt to fetch from DB
//    Task<Person> user = fetchFromCache(id)
//         .recoverWith(e -> fetchFromDB(id));

//withtimeout-1
    final Task<Response> google = HttpClient.get("http://google.com").task()
        .withTimeout(10, TimeUnit.MILLISECONDS);

    engine.run(google);

    google.await();

    ExampleUtil.printTracingResults(google);
  }

  Task<Person> fetchFromCache(Long id) {
    return Task.callable("fetchFromCache", () -> { throw new Exception(); });
  }

  Task<Person> fetchFromDB(Long id) {
    return Task.callable("fetchFromDB", () -> { return null; });
  }

  Task<Person> fetchPerson(Long id) {
    return Task.callable("fetchPerson", () -> { throw new Exception(); });
  }

  private class MyLibException extends Exception {
    public MyLibException(Throwable error) {
      super(error);
    }
  };

  private Task<?> updateMemcache(String u) {
    return Task.callable("updateMemcache", () -> "");
  }

  private Task<String> fetch(URI uri) {
    return Task.callable("fetch", () -> "");
  }

  private Task<String> fetch(Long id) {
    return Task.callable("fetch", () -> "");
  }
}
