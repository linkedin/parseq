package com.linkedin.parseq;

import java.io.File;

public class TracevisServerMain {

  public static void main(String[] args) throws Exception {

    if (args.length < 1 || args.length > 2) {
      System.out.println("Incorrect arguments, expecting: DOT_LOCATION <PORT>\n"
          + "  DOT_LOCATION - location of graphviz dot executable\n"
          + "  <PORT>       - optional port number, default is " + Constants.DEFAULT_PORT);
    }
    final String dotLocation = args[0];
    final int port = (args.length == 2) ? Integer.parseInt(args[1]) : Constants.DEFAULT_PORT;

    new TracevisServer(dotLocation, port, new File("./").toPath(), Constants.DEFAULT_CACHE_SIZE, Constants.DEFAULT_TIMEOUT_MS)
      .start();
  }

}
