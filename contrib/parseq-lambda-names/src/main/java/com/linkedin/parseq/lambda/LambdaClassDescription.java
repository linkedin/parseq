package com.linkedin.parseq.lambda;

public class LambdaClassDescription {

  private final String _className;
  private final SourcePointer _sourcePointer;

  public LambdaClassDescription(String className, SourcePointer sourcePointer) {
    _className = className;
    _sourcePointer = sourcePointer;
  }

  public String getDescription() {
    return _sourcePointer.toString();
  }

  String getClassName() {
    return _className;
  }

  @Override
  public String toString() {
    return _className + " => " + getDescription();
  }
}
