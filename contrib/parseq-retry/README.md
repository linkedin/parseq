ParSeq Retry
============

Sometimes, especially when network calls are involved, tasks could fail for random reasons and in many cases simple retry would fix the problem. ParSeq Retry provides a flexible mechanism for configuring a retry behavior, allowing your application to be more resilient to intermittent failures.

Examples
========

The most simple example provides basic retry functionality:

```java
import static com.linkedin.parseq.retry.RetriableTask.withRetryPolicy;

Task<String> task = withRetryPolicy(RetryPolicy.simple(3), () -> Task.value("Hello, World!"));
```

It's possible for the task generator to take the current attempt number (zero-based): 

```java
Task<String> task = withRetryPolicy(RetryPolicy.simple(3), attempt -> Task.value("Current attempt: " + attempt));
```

It's also recommended to specify the operation name, so that it can be used for logging and naming of ParSeq tasks:

```java
Task<String> task = withRetryPolicy("sampleOperation", RetryPolicy.simple(3), () -> Task.value("Hello, World!"));
```

Error and result classification
===============================

Not every task failure is intermittent and not every task result is valid. Retry policy can be configured to do error and/or result classification:

```java
Function<Throwable, ErrorClassification> errorClassifier = error -> error instanceof TimeoutException ? ErrorClassification.RECOVERABLE : ErrorClassification.FATAL;
Function<Integer, ResultClassification> resultClassifier = result -> result == 0 ? ResultClassification.UNACCEPTABLE : ResultClassification.ACCEPTABLE;
Task<Integer> task = withRetryPolicy(RetryPolicy.<Integer>simple(3).setResultClassifier(resultClassifier).setErrorClassifier(errorClassifier), () -> Task.value(Random.nextInt(10)));
```

Unacceptable results are recoverable by default, but it can also be changed:
```java
ResultClassification.UNACCEPTABLE.setStatus(ErrorClassification.FATAL);
```

There is also a ```ErrorClassification.SILENTLY_RECOVERABLE``` value which suppresses logging during retry operations. It does not suppress logging of fatal failures.

Backoff policies
================

Simple retry policy from the examples above would retry failed tasks immediately. Sometimes it's not the best approach and variable delays would produce higher success ratios. It's possible to configure backoff policies:

```java
BackoffPolicy<String> backoffPolicy = BackoffPolicy.<>constant(1000);
Task<String> task = withRetryPolicy(RetryPolicy.<String>simple(3).setBackoffPolicy(backoffPolicy), () -> Task.value("Hello, World!"));
```

There are several backoff policies available: ```constant```, ```linear```, ```exponential```, ```fibonacci```, ```randomized```, ```selected```. It's also possible to create your own by implementing ```BackoffPolicy``` interface.

Termination policies
====================

To configure the number of retry attempts there are a few termination policies available:

```java
TerminationPolicy terminationPolicy = TerminationPolicy.limitDuration(1000);
Task<String> task = withRetryPolicy(RetryPolicy.<String>simple(3).setTerminationPolicy(terminationPolicy), () -> Task.value("Hello, World!"));
```

The ```limitDuration``` policy would limit the total duration of the task (including all retries) to the provided number of milliseconds. Be careful: if the task fails fast, that could mean a lot of retries!

Other available termination policies include ```requireBoth```, ```requireEither```, ```alwaysTerminate``` and ```neverTerminate```. It is possible to configure your own by implementing ```TerminationPolicy``` interface.

Event monitoring
================

By default retry task wrapper does not do any logging of its progress. Failed task would hold a ```Throwable``` object and it's up to the caller to process that. But sometimes it's useful to log retry task process. There are several logging mechanisms provided, the simplest one is the console logging:

```java
EventMonitor<String> eventMonitor = EventMonitor.<>printWithStream(System.out);
Task<String> task = withRetryPolicy(RetryPolicy.<String>simple(3).setEventMonitor(eventMonitor), () -> Task.value("Hello, World!"));
```

Instead of ```PrintStream``` it's possible to use ```PrintWriter```:
```java
EventMonitor<String> eventMonitor = EventMonitor.<>printWithWriter(somePrintWriter);
```

In addition to that there is also support for two common logging mechanisms - java logging ```EventMonitor.logWithJava(javaLogger)``` and slf4j ```EventMonitor.logWithSlf4j(slf4jLogger)```. It is also possible to chain multiple event monitors together using ```EventMonitor.<>chained(firstMonitor, secondMonitor)```. For custom loggers it is possible to implement ```EventMonitor<>``` interface.
