package com.linkedin.parseq;

import java.io.File;

public class TracevisServerMain {

  public static void main(String[] args) throws Exception {

    if (args.length < 2 || args.length > 3) {
      System.out.println("Incorrect arguments, expecting: DOT_LOCATION TRACEVIS_LOCATION <PORT>\n"
          + "  DOT_LOCATION      - location of graphviz dot executable\n"
          + "  TRACEVIS_LOCATION - location of tracevis"
          + "  HEAPSTER_LOCATION - location of heapster"
          + "  <PORT>            - optional port number, default is " + Constants.DEFAULT_PORT);
      System.exit(1);
    }
    final String dotLocation = args[0];
    final int port = (args.length == 4) ? Integer.parseInt(args[3]) : Constants.DEFAULT_PORT;

    new TracevisServer(dotLocation, port, new File(args[1]).toPath(), new File(args[2]).toPath(), Constants.DEFAULT_CACHE_SIZE, Constants.DEFAULT_TIMEOUT_MS)
      .start();
  }

}
