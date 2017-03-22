package com.linkedin.parseq.lambda;

class InferredOperation {

  private final String _functionName;

  InferredOperation(String functionName) {
    _functionName = functionName;
  }

  @Override
  public String toString() {
    return _functionName == null ? "" : _functionName;
  }
}
