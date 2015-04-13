ParSeq Trace Visualization Server
==========================

This project includes a trace visualization server for
[https://github.com/linkedin/parseq](ParSeq) traces.


Building
========

To build the trace visualization server, use `mvn package`. This creates a runnable jar file under `target/parseq-tracevis-server-jar-with-dependencies.jar`.


Running the Trace Visualization Server
============================

First install [graphviz](http://www.graphviz.org/) e.g. on mac you might run `brew install graphviz`.

Find path to a `dot` executable. `dot` is part og graphviz installation e.g. `which dot`.

Run server passing path to `dot` e.g. `java -jar parseq-tracevis-server-jar-with-dependencies.jar /usr/bin/dot`.

You can otionally specify port number, by default it will run on port 8080.


More Info
=========

For more information, see the [ParSeq trace wiki](https://github.com/linkedin/parseq/wiki/Tracing).


License
=======

This tool is licensed under the terms of the Apache License, Version 2.0.
