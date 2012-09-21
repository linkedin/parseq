ParSeq
======

ParSeq is a framework that makes it easier to write and maintain fast, scalable
applications in Java.

Some of the key benefits of ParSeq include:

* Parallelization of asynchronous operations (such as IO)
* Serialized execution for non-blocking computation
* Code reuse via task composition
* Simple error propagation and recovery
* Execution tracing and visualization

The remainder of this README provides a very basic introduction to ParSeq. We
recommend looking at the ParSeq wiki for more details.

Key Concepts
------------

In ParSeq, we have a few basic concepts:

* *Promise*: like a Java Future, a Promise allows the user to get the result of
  an asynchronous computation. However, a Promise allows the user to wait for
  the result asynchronously instead of requiring a blocking `get` call.
* *Task*: a Task is an action that can be scheduled with an Engine (see below).
  Tasks can be sequenced using `seq` and `par` (see below).
* *par*: composes a group of Tasks that can be executed in parallel.
* *seq*: composes an ordered list of Tasks that will be executed sequentially.
* *Engine*: a pool of workers that executes Tasks.

Example Usage
-------------

For this example, suppose we want to get the home page for a few popular
browsers in parallel and then combine them. In ParSeq, we would code this up as
follows:


    final Task<String> google = httpClient.fetch("http://www.google.com");
    final Task<String> bing = httpClient.fetch("http://www.bing.com");
    final Task<String> yahoo = httpClient.fetch("http://www.yahoo.com");

    final Task<String> combination = new BaseTask<String>() {
        public Promise<String> run(Context ctx) {
            String googleStr = google.get();
            String bingStr = bing.get();
            String yahooStr = yahoo.get();

            // Build some combination out of the above three strings.

            return result;
        }
    };

    Task<String> tasks = Tasks.seq(Tasks.par(google, bing, yahoo),
                                   combination);
    engine.run(tasks);

This will first fetch the URL for various pages in parallel and after they have
all been retrieved it will combine them using the `combination` task.

For many more examples, please see the `example` module in the source code.

What Next?
----------

To learn more about ParSeq, please visit our Wiki.

Build Status
------------

[![Build Status](https://secure.travis-ci.org/linkedin/parseq.png)](http://travis-ci.org/linkedin/parseq.js)

License
-------

ParSeq is licensed under the terms of the [Apache License, Version
2.0](http://www.apache.org/licenses/LICENSE-2.0).
