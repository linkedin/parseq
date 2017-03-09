package com.linkedin.parseq.lambda;

class InferredOperation {

  final int _opcode;
  final String _functionName;

  InferredOperation(int opcode, String functionName) {
    _opcode = opcode;
    _functionName = functionName;
  }

  @Override
  public String toString() {
    //opcode is primarily for debugging purposes, not making that a part of description
    return _functionName == null ? "" : _functionName;
  }
}
