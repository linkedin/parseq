v3.0.4
------



v3.0.3
------

* Make ParSeq batch get requests with exactly the same key type

v3.0.2
------

* Configure unit test in Gradle

v3.0.1
------

* Use relative path for javadoc images

v3.0.0
------

* Update README file and fix vulnerable dependencies in package.json
* Migrate to Gradle build automation

v2.6.36
------

* Make sure that ParSeqRestClient timeout configuration does not impact lower-lever R2D2 timeout logic.

v2.6.35
------

* Remove .DS_Store file and improve javadoc for Task.par.
* Fix Tuple*Task.java javadoc's image not showing bug 

v2.6.34
------

* Use D2 per-request timeout if enabled.

v2.6.33
------

* Allow - in parent resource name in parseq-restli-client configuration for cross-center calls.
* Add Zookeeper ACL support

v2.6.32
------

* Add javadoc to ParSeqRestClient to explain error handling.
* Update README for parseq-restli-client maxBatchSize configuration limitation on existing BATCH_GET.
* Fix tracevis name truncation bug
* Attach version number to tracevis server jar

v2.6.31
------

* PlanCompletionListener should be invoked after batch task is finished.

v2.6.30
------

* Bridge between Task and CompletionStage.

v2.6.29
------

* Increase the arity of Task.par() to support 15 parameters.
* Standardize indentation by 2 spaces on all pom.xml files.

v2.6.28
------

* Fix StackOverflowError in RequestConfigTree.

v2.6.27
------

* Use typesafe method to build projection for single get.

v2.6.26
------

* Use typesafe batch request builder method to build batch_get.
* Support sub-resource level configuration in ParSeqRestClient.

v2.6.25
------

* Adds back ParSeqRestClient(RestClient) constructor to maintain binary backward-compatibility.

v2.6.24
------

* Adds back #setRestClient method with RestClient as the parameter to maintain binary backward-compatibility.

v2.6.23
------

* Change the underlying client of ParSeqRestClient from the RestClient class to the Client interface.

v2.6.22
------

* Improved generation of task descriptions for deeply nested structures, see issue #145 on Github.

v2.6.21
------

* Include description of the task that timed out in TimeoutException created by Task.withTimeout().

v2.6.20
------

* Fixed bug which caused logging idle threads as busy ones in Execution Monitoring.

v2.6.19
------

* Improved ExecutionMonitor by taking into consideration shortest observable delta between scheduled wake up and actual wake up.

v2.6.18
------

* Added Execution Monitoring - mechanism which allows detecting long running tasks and potential programming bugs.

v2.6.17
------

* Added ParSeqUnitTestHelper.tearDown(int, TimeUnit) method which waits for specified amount of time for all currently running plans to complete.

v2.6.16
------

* Bug fix: Removing ea agents jar from being shaded as part of parseq-lambda-names jar.
* Created ParSeqUnitTestHelper helper class for unit tests so that it is not necessary to extend BaseEngineTest class.
* Created BaseEngineParTest for parallel unit tests.
* Added more unique set up and tear down methods in BaseEngineTest to avoid accidental override in subclasses.

v2.6.15
------

* Improved exception in case when function that is supposed return a Task instance returns null, see issue #105. From now on exception's stack trace and message will inform about the root cause of a problem.
* Increased trace size limit from 4096 to 65536. Trace size limit is a heuristic that prevents allocating huge amount of memory for the purpose of tracing.
* Publish parseq-benchmark artifact to maven central.

v2.6.14
------

* Fixing line number for method invocations in parseq-lambda-names contrib project.

v2.6.13
------

* Upgraded dependency on pegasus to 11.0.0 in parseq-restli-client contrib project
* Create 404 synthetic result when batch get request does not return any response for an id

v2.6.12
------

* Adding contrib project parseq-lambda-names
  - The project aims to provide more meaningful default descriptions for Parseq tasks. Using ASM, it tries to locate where lambda expression is defined in source code and also infer some details about its execution like function call within lambda expression with number of arguments.
  - Using task descriptor in Task interface to infer task description

v2.6.11
------

* Significantly reduce Task creation overhead when cross-thread stack tracing is enabled.

v2.6.10
------

* Performance optimizations:
  - Eagerly drain task queue in SerialExecutor. The goal of this optimization is to avoid expensive context switches and improve memory cache utilization for the price of "fairness". Since this version the default behavior is to drain task queue eagerly. The previous behavior can be enabled by setting Engine configuration property: `Engine.DRAIN_SERIAL_EXECUTOR_QUEUE` to `true`.
  - Disable trampoline. Trampoline is a mechanism that allows avoiding stack overflow. It is not without a cost and for certain workflows it is worth turning it off. Since this version trampoline is disabled. It can be enabled using `ParSeqGlobalConfiguration.setTrampolineEnabled()`.
  - Use LIFOBiPriorityQueue as a task queue implementation in SerialExecutor. LIFOBiPriorityQueue is a task queue that recognizes only two priorities which allows faster implementation. It also uses LIFO order which can improve memory cache utilization. It is possible to use previous implementation by default by setting Engine configuration property: `Engine.DEFAULT_TASK_QUEUE` to `FIFOPriorityQueue.class.getName()`.
  - Avoid creation and copying of arrays in TaskParImpl.
  - Tracing improvements. Removed reference counting and replaced usage of HashMaps with ArrayLists.
* Added benchmarks that can be used for testing ParSeq performance. This is just a beginning of work on more reliable and automated performance tests for ParSeq.

v2.6.9
------

* Deprecate Tasks.par(...) for safer alternative Task.par(...) that does not throw IllegalArgumentException on empty collection.
* Enable automatic cross-thread stack tracing. It is an optional feature, turned off by default. See `ParSeqGlobalConfiguration.setCrossThreadStackTracesEnabled()`.

v2.6.8
------

* Fixed unbatching various types of BATCH_GET requests from BATCH_GET for complex keys.

v2.6.7
------

* Fixing test in TestRequestContextProvider

v2.6.6
------

* Adding a notion of taskType in ShallowTrace. The idea is to set a type (string) to tasks such as timerTask, withSideEffectTask, remoteTask etc. This tag would be displayed in trace visualization.
* Added RequestContext provider to ParSeqRestClient

v2.6.5
------

* Added compile and master configurations to parseq's master ivy file to make it interact correctly with ivy files automatically generated from maven's pom files.

v2.6.4
------

* Fixed unbatching GET from BATCH_GET for complex keys.

v2.6.3
------

* Handle uncaught exception in SerialExecutor more gracefully

v2.6.2
------

* Allow user to wire custom TaskQueue implementation into SerialExecutor
* Refactoring ZKClient in parseq-zk-client contrib project to use interface + builder pattern

v2.6.1
------

* Fixed bug which caused shareable, batchable tasks to hang

v2.6.0
------

* Added tasks with retry policy: Task.withRetryPolicy(...)

v2.5.0
------

* Added _MaxConcurrentPlans_ configuration parameter to Engine that enforces limit on how many concurrent Plans can be executed by Engine.

v2.4.2
------

* Fixed backwards incompatible changes to Trace and TraceBuilder introduced in 2.4.0.

v2.4.1
------

* Fixed backwards incompatible change to EngineBuilder introduced in 2.4.0.

v2.4.0
------

* Added flexible timeout and batching configuration to ParSeqRestClient.
* Allow adding description to timeout task and TimeoutException.
* Renamed "timeoutTimer" task to "timeout".
* Truncating task names to 1024 characters.
* Decoupled batch size from number of keys in the batch.
* Added critical path filtering to Tracevis.
* Added PlanCompletionListener.

v2.3.4
------

* Fixed type of response for BatchEntityResponse in batching restli client

v2.3.3
------

* Fixed unwrapping EntityResponse from batch get response in batching restli client

v2.3.2
------

* Trace.toString() now returns JSON representation that can be directly used in trace visualization tools
* Significant progress towards fully functional batching restli client

v2.3.1
------

* Added convenience methods to BaseEngineTest

v2.3.0
------

* Added Task based batching strategy to parseq-batching contrib project. It allows defining strategy using Task API.
* Fixed bug in setting system hidden attribute on fused task.

v2.2.0
------

* Added parseq-batching contrib project. It allows automatic batching of asynchronous operations based on user defined strategy.
* Fixed bug in tracevis that caused filtering option not work correctly for certain types of graphs.
* Added tooltip to Graphviz view in tracevis.
* Improved documentation of SerialExecutor.

v2.1.2
------

* Fixed bug in ShallowTraceBuilder that could cause NPE when getTrace() was called and task was cancelled at the same time.

v2.1.1
------

* Fixed bug in recovery methods when task was cancelled.

v2.1.0
------

* Tracing improvements:
  - Changed the way tasks fusion is reflected in trace so that it is more intuitive and better reflects performance of individual transformations
  - Added class name of lambdas passed to functional API to default task descriptions
  - Changed ParTaskImpl to not be system hidden

v2.0.8
------

* Revert behavior of Task.andThen(Task) to pre-2.0.7 because fix implemented in 2.0.7 is a backwards incompatible change. Change of semantics of Task.andThen(Task) will be revisited on next major version upgrade.


v2.0.7
------

* Fix bug which caused that in expression first.andThen(second) second was executed even if first task failed.

v2.0.6
------

* Include stack trace in the trace's value for failed tasks.
* Add checking for null values in various methods of Task.

v2.0.5
------

* Updated dependencies of contrib projects

v2.0.4
------

* Fixed invalid svg url in Graphviz view when html has base tag
* Fixed Content-type of POST to dot: application/json replaced with text/plain
* Fixed layout of zoom control in Graphviz view
* Fixed typos
* Generalized TracevisServer into a separate GraphvisEngine which can be used by many frameworks e.g. jetty or play

v2.0.3
------

* Fix NPE in ShallowTraceBuilder copy constructor.

v2.0.2
------

* Added version number to tracevis (issue #56).
* Degrade gracefully instead of showing error pop-up when tracevis-server is not accessible (issue #57).
* Added spellcheck="false" to textarea where JSON trace is pasted (issue #36).
* Added TraceUtil class to simplify generating JSON trace for a task.
* Fixed Javadoc to display better in Intellij IDEA.
* Internal implementation changes: simplified continuations implementation.

v2.0.1
------

Fixes bug which caused errors propagated by flatMap to be nested in PromiseException

v2.0.0
------

* Introduces new "functional" API in Task interface and deprecated most of v1.x API
* Added contrib folder for complementary projects:
  - parseq-examples with v2.0 API examples
  - parseq-legacy-exampels with v1.x API examples
  - parseq-http-client that provides integration with Async Http Client library
  - parseq-exec that provides integration with Java Process API
  - parseq-tracevis-server that serves tracevis tool and is capable of rendering graphviz diagrams
* Refactored tracing mechanism:
  - each task has unique id
  - added configurable limit on number of relationships that are part of a trace
    this allows tracing large plans (millions of tasks)
  - gracefully handle cycles in generated trace
  - traces of all tasks belonging to the same plan are equal
  - tasks trace is a trace of a plan that executed it
  - tasks value is not included by default it (see Task.setTraceValueSerializer())
* Changed logging API so that it is possible to pass planClass to Engine.run()
* Improved reliability of promise propagation to avoid stack overflow in large plans (millions of tasks)
* Introduced blocking() method which allows integration of blocking APIs using multiple dedicated Executors
* Tracevis improvements:
  - support for server-side graphviz diagrams generated by parseq-tracevis-server
  - added zooming/panning in graphviz-view
  - added "time slider" to graphviz-view
* Added shareable() method to allow sharing tasks among plans (avoid automatic cancellation)
* Introduced FusionTask that improves performance of non-blocking synchronous transformations

v1.4.2
------

* Added side effect task.

v1.4.1
------

* Removed hashCode() and equals() from TraceImpl to speed up trace creation.

v1.4.0
------

* Numerous improvements to the tracevis tool

v1.3.7
------

* We now cancel a plan if its execution fails due to a
  RejectedExecutionException being raised from the engine's task executor.

v1.3.6
------

* Fixes to tracevis packaging.

v1.3.5
------

* Tracevis bug fix.

v1.3.4
------

* Publish parseq-tracevis package.
* Minor fixes to tests and examples.

v1.3.3
------

* Added "ThrowableCallable" a Callable that allows a Throwable to be thrown
* Use slf4j-api to replace slf4j-log4j12 in build dependency.

v1.3.2
------

* Changed BaseTask.run(Context) to throw Throwable instead of Exception.

v1.3.1
------

* Performance improvements (i.e. quicker garbage collection) for timer tasks.

v1.3.0
------

* Improvements to waterfall trace visualization including the ability to
  collapse composite tasks.
* BaseTask will now catch Throwable instead of Exception. Any Throwable
  caught will fail the task.

v1.2.0
------

* Added AsyncCallableTask which makes it possible to use blocking tasks
  asynchronously. The intention is to use this for tasks that involve
  blocking IO. Thanks to [cheftako](https://github.com/cheftako) for the contribution!

v1.1.0
------

* Removed vararg methods for `Tasks.par` and `Tasks.seq`. In their place
  we've added type-safe alternatives that take up to 10 tasks. This change
  is not backward compatible for cases where an array was supplied to the
  varargs version of `Tasks.par` and `Tasks.seq` or where a single task
  was given to `Tasks.par` or `Tasks.seq`.
* Made JsonTraceDeserializer package private - it is not for public
  consumption
* Miscellaneous Javadoc improvements

v1.0.0
------

* Minor performance improvements.

v0.4.5
------

* Add visualization support to hide all parent tasks.
* PromiseListener.onResolved(..) now has the Promise that was completed as a
  parameter.

v0.4.4
------

* Trace output now contains relationship of potential parents. A potential
  parent relationship is defined as a parent task running a child task that has
  already been completed.
* Dag visualization now includes:
    * Dash edge between a potential parent to an potential child task.
    * Dash edge between a predecessor to a successor task that has been
      completed.
    * Solid edge between a potential parent to an potential child sink task.

v0.4.3
------

* No changes

v0.4.2
------

* Dist target now includes a tarball for tracevis.
* Added support for system hidden task with support for visualization. User
  defined hidden task should use ShallowTrace.setHidden(...).

v0.4.1
------

* Misc code hygiene improvements

v0.4.0
------

* BACKWARD INCOMPATIBLE:
    * Removed Tasks.async. Use BaseTask instead.
    * Renamed Tasks.sync to Tasks.callable.
    * Removed Tasks.value which was only used for test purposes.
    * Removed getStartNanos() and getEndNanos() from Task. Instead use
      Task.getShallowTrace() which returns a ShallowTrace that contains the
      getStartNanos() and getEndNanos().
    * Engine creation has changed:
        * Replace:
            new Engine(taskExecutor, timerExecutor)
        * With:
            new EngineBuilder()
                .setTaskExecutor(taskExecutor)
                .setTimerExecutor(timerExecutor)
                .build();
  * Engine.awaitTermination(...) provides a mechanism to wait for the engine
    to shutdown.
* Visualization improvements / fixes:
    * Table: use a textarea for values
    * Waterfall: use nanosecond precision for laying out bars
    * Don't include value.toString() in task name for value task.
* Logging
    * We now provide three loggers to collect task information at runtime:
        * com.linkedin.parseq.Engine:all - logs all tasks
        * com.linkedin.parseq.Engine:root - logs root tasks only
        * com.linkedin.parseq.Engine:planClass=xyz - When xyz is a root
          class it and all of its descendants are logged.
    * We provide two log levels with these loggers:
        * DEBUG - logs task name and result type
        * TRACE - logs task name, result type, and value
* Added Tasks.seq to support Iterable<Tasks<?>> as a parameter.
* Added support for hidden trace to indicate if it should be displayed in
  the visualization.
* Added attributes to trace such that additional information can be added.
* Added TraceBuilder to support customize Trace information.

v0.3.0
------

* Added support for priorities to Tasks. Task priorities only influence
  ordering in a particular context.
* Remove the existing waterfall trace visualization and replace it with a
  more scalable and interactive javascript visualization.
* Rename BaseTask.run(...) to BaseTask.contextRun(...) and
  BaseTask.doRun(...) to BaseTask.run(...) to better match the method
  purpose.
* Replace existing trace printers with javascript based equivalents.
* Dag trace now includes the start time of the task.
* Added par(Iterable<Task<T>> tasks) to the Tasks class. The new par(...) will
  return the result of each of the supplied tasks. The ParTask will fail
  if any of the executed task fails. Additional methods are available for
  ParTask:
    * getTasks() for the set of tasks related to ParTask.
    * getSuccessful() to get the values of successfully executed tasks.
* Updated par(...) to return ParTask.

v0.2.1
------

* Misc code hygiene improvements

v0.2.0
------

* Trace improvements
    * Rename TaskTrace to Trace
    * Move JsonTraceCodec to com.linkedin.parseq.trace.codec.json
    * Move trace printers to com.linkedin.parseq.trace.printer
    * More compact JSON serialization for traces:
        * Don't include null or empty fields
        * Render each trace once
        * Set up edges independently of the traces
    * Tasks now use null to indicate no value for startNanos and endNanos
    * Traces are now fully immutable and thread-safe. Use TraceBuilders to
      create new traces or to copy and edit existing ones.

* Task construction improvments
    * Remove TaskDefs (use Tasks.action, Tasks.sync, Tasks.async, and new
      BaseTask()) in their place
    * Added ValueTask - returns a pre-determined value upon execution
    * Remove Named. The Tasks factory methods take a name and Task has a
      constructor that takes a name. If the empty Task constructor is used
      then getName() will return the value of toString() unless it has been
      overridden.

v0.1.0
------

* Promises and Tasks now take Throwables instead of Exceptions. This
  provides better interoperability with frameworks like Pegasus and Play.
* Trace creation has changed:
    * Old: TaskTraces.convertToTaskTrace(trace)
    * New: task.getTrace()
* Remove/rename "assembler"
    * Move com.linkedin.parseq.assembler.trace to com.linkedin.parseq.trace
    * Move all classes in com.linkedin.parseq.assember to com.linkedin.parseq
    * Rename Assembler to Engine
    * Replace references to "assembler" with "engine"
