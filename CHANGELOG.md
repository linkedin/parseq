v1.3.4
------

* Renamed trace.html in tracevis tool to index.html

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
