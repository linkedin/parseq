ParSeq Trace Visualization Server
==========================

This project includes a trace visualization server for
[https://github.com/linkedin/parseq](ParSeq) traces.


Building
========

To build the trace visualization server, use `./gradlew build`. This creates a runnable jar file under `build/libs/parseq-tracevis-server-jar-with-dependencies.jar`.


Downloading
===========

You can download latest version of `parseq-tracevis-server-jar-with-dependencies.jar` from [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.linkedin.parseq%22%20AND%20a%3A%22parseq-tracevis-server%22).


Running the Trace Visualization Server
======================================

First install [graphviz](http://www.graphviz.org/) e.g. on mac you might run `brew install graphviz`.

Find path to a `dot` executable. `dot` is part of graphviz installation e.g. `which dot`.

Run server passing path to `dot` as an argument e.g. `java -jar parseq-tracevis-server-jar-with-dependencies.jar /usr/bin/dot`.

(Alternative) After graphviz installation, just run `./gradlew runTracevisServer`

You can optionally specify port number, by default it will run on port 8080.


Docker
======================================

To start tracevis server using docker: `docker run -d -p 8080:8080 jodzga/parseq-tracevis-server:latest`. The server is accessible at [http://localhost:8080](http://localhost:8080).

More Info
=========

For more information, see the [ParSeq trace wiki](https://github.com/linkedin/parseq/wiki/Tracing).


License
=======

This tool is licensed under the terms of the Apache License, Version 2.0.
