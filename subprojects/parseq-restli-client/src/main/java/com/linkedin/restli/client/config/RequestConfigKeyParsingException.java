package com.linkedin.restli.client.config;

class RequestConfigKeyParsingException extends Exception {

  public RequestConfigKeyParsingException(String message) {
    super(message);
  }

  public RequestConfigKeyParsingException(Exception e) {
    super(e);
  }

  private static final long serialVersionUID = 1L;

}
