Running Visualization
=====================

To run the visualizations in your browser, add this directory to any web
server.

Probably the easiest approach is to use Python's SimpleHTTPServer from
this directory. To start SimpleHTTPServer on port 8888 use:

```sh
python -m SimpleHTTPServer 8888
```

Then connect to [[http://localhost:8888/trace.html]].

Running Unit Tests for Visualization
====================================

The unit tests requires node package manager (npm). Once it is installed, use
`make test` to run the tests.
