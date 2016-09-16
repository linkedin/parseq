ParSeq Retry
============

Sometimes, especially when network calls are involved, tasks could fail for random reasons and in many cases simple retry would fix the problem. ParSeq Retry provides a flexible mechanism for configuring a retry behavior, allowing your application to be more resilient to intermittent failures.

Examples
========

The most simple example provides basic retry functionality:

```java
import static com.linkedin.parseq.retry.RetriableTask.withRetryPolicy;

Task<String> task1 = withRetryPolicy(RetryPolicy.attempts(3), () -> Task.value("Hello, World!"));
Task<String> task2 = withRetryPolicy(RetryPolicy.duration(5000), () -> Task.value("Hello, World!"));
```

It's possible for the task generator to take the current attempt number (zero-based): 

```java
Task<String> task = withRetryPolicy(RetryPolicy.attempts(3), attempt -> Task.value("Current attempt: " + attempt));
```

It's also recommended to specify the operation name, so that it can be used for logging and naming of ParSeq tasks:

```java
Task<String> task = withRetryPolicy("sampleOperation", RetryPolicy.attempts(3), () -> Task.value("Hello, World!"));
```

Instead of using predefined ```RetryPolicy``` helpers it's possible to use a builder class to achieve the same effect:

```java
RetryPolicy<String> retryPolicy = new RetryPolicyBuilder<String>()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .build();
```

Error and result classification
===============================

Not every task failure is intermittent and not every task result is valid. Retry policy can be configured to do error and/or result classification:

```java
Function<Throwable, ErrorClassification> errorClassifier = error -> error instanceof TimeoutException ? ErrorClassification.RECOVERABLE : ErrorClassification.FATAL;
Function<Integer, ResultClassification> resultClassifier = result -> result == 0 ? ResultClassification.UNACCEPTABLE : ResultClassification.ACCEPTABLE;
RetryPolicy<String> retryPolicy = new RetryPolicyBuilder<String>()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .setResultClassifier(resultClassifier)
    .setErrorClassifier(errorClassifier)
    .build();
Task<Integer> task = withRetryPolicy(retryPolicy, () -> Task.value(Random.nextInt(10)));
```

Unacceptable results are recoverable by default, but it can also be changed:

```java
ResultClassification.UNACCEPTABLE.setStatus(ErrorClassification.FATAL);
```

There is also a ```ErrorClassification.SILENTLY_RECOVERABLE``` value which suppresses logging during retry operations. It does not suppress logging of fatal failures.

Termination policies
====================

To configure the number of retry attempts there are a few termination policies available:

```java
RetryPolicy<String> retryPolicy = new RetryPolicyBuilder<String>()
    .setTerminationPolicy(TerminationPolicy.limitDuration(1000))
    .build();
Task<String> task = withRetryPolicy(retryPolicy, () -> Task.value("Hello, World!"));
```

The ```limitDuration``` policy would limit the total duration of the task (including all retries) to the provided number of milliseconds. Be careful: if the task fails fast, that could mean a lot of retries!

Other available termination policies include ```requireBoth```, ```requireEither```, ```alwaysTerminate``` and ```neverTerminate```. It is possible to configure your own by implementing ```TerminationPolicy``` interface.

NOTE: When building a retry policy, there should be always some termination policy specified, otherwise exception will be thrown.

Backoff policies
================

Simple retry policy from the examples above would retry failed tasks immediately. Sometimes it's not the best approach and variable delays would produce higher success ratios. It's possible to configure backoff policies:

```java
RetryPolicy<String> retryPolicy = new RetryPolicyBuilder<String>()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .setBackoffPolicy(BackoffPolicy.<>constant(1000))
    .build();
Task<String> task = withRetryPolicy(retryPolicy, () -> Task.value("Hello, World!"));
```

There are several backoff policies available: ```constant```, ```linear```, ```exponential```, ```fibonacci```, ```randomized```, ```selected```. It's also possible to create your own by implementing ```BackoffPolicy``` interface.

Event monitoring
================

By default retry task wrapper does not do any logging of its progress. Failed task would hold a ```Throwable``` object and it's up to the caller to process that. But sometimes it's useful to log retry task process. There are several logging mechanisms provided, the simplest one is the console logging:

```java
RetryPolicy<String> retryPolicy = new RetryPolicyBuilder<String>()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .setEventMonitor(EventMonitor.<>printWithStream(System.out))
    .build();
Task<String> task = withRetryPolicy(retryPolicy, () -> Task.value("Hello, World!"));
```

Instead of ```PrintStream``` it's possible to use ```PrintWriter```:
```java
EventMonitor<String> eventMonitor = EventMonitor.<>printWithWriter(somePrintWriter);
```

In addition to that there is also support for two common logging mechanisms - java logging ```EventMonitor.logWithJava(javaLogger)``` and slf4j ```EventMonitor.logWithSlf4j(slf4jLogger)```. It is also possible to chain multiple event monitors together using ```EventMonitor.<>chained(firstMonitor, secondMonitor)```. For custom loggers it is possible to implement ```EventMonitor<>``` interface.
