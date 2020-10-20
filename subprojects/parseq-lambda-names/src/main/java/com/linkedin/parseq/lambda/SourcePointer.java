package com.linkedin.parseq.lambda;

import java.util.Arrays;
import java.util.Optional;

class SourcePointer {

  String _className;
  String _callingMethod;
  int _lineNumber;

  private SourcePointer(String className, String methodName, Integer lineNumber) {
    _className = className;
    _callingMethod = methodName;
    _lineNumber = lineNumber;
  }

  /* package private */ static Optional<SourcePointer> get(Exception exception) {
    //create an exception, discard known elements from stack trace and find first element with suspect
    return Arrays.stream(exception.getStackTrace())
        .filter(SourcePointer::notLambdaStuff)
        .findFirst()
        .map(SourcePointer::sourcePointer);
  }

  private static boolean notLambdaStuff(StackTraceElement element) {
    return !(element.getClassName().startsWith("java.")
        || element.getClassName().startsWith("sun.")
        || element.getClassName().startsWith("org.objectweb.asm.")
        || element.getClassName().startsWith("jdk.")
        || element.getMethodName().startsWith("lambda$")
        || element.getClassName().contains("$$Lambda$")
        || element.getClassName().startsWith(ASMBasedTaskDescriptor.class.getName()));
  }

  private static SourcePointer sourcePointer(StackTraceElement stackTraceElement) {
    return new SourcePointer(stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
        stackTraceElement.getLineNumber());
  }

  public void setLineNumber(int lineNumber) {
    _lineNumber = lineNumber;
  }

  public void setClassName(String className) {
    _className = className;
  }

  public void setCallingMethod(String callingMethod) {
    _callingMethod = callingMethod;
  }

  public String getClassName() {
    return _className;
  }

  @Override
  public String toString() {
    String classAndLine = Util.extractSimpleName(_className, ".") + (_lineNumber > 0 ? ":" + _lineNumber : "");
    if (_callingMethod != null) {
      return _callingMethod + "(" + classAndLine + ")";
    } else {
      return classAndLine;
    }
  }
}
