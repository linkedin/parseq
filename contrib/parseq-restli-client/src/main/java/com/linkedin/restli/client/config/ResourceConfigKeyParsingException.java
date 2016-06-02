package com.linkedin.restli.client.config;

class ResourceConfigKeyParsingException extends Exception {

  public ResourceConfigKeyParsingException(String message) {
    super(message);
  }

  public ResourceConfigKeyParsingException(Exception e) {
    super(e);
  }

  private static final long serialVersionUID = 1L;

}
