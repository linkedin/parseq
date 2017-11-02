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

  /* package private */ static Optional<SourcePointer> get() {
    //create an exception, discard known elements from stack trace and find first element with suspect
    return Arrays.stream(new Exception().getStackTrace())
        .filter(SourcePointer::notLambdaStuff)
        .findFirst()
        .map(SourcePointer::sourcePointer);
  }

  private static boolean notLambdaStuff(StackTraceElement element) {
    return !(element.getClassName().startsWith("java.")
        || element.getClassName().startsWith("sun.")
        || element.getClassName().startsWith("org.objectweb.asm.")
        || element.getClassName().startsWith(ASMBasedTaskDescriptor.class.getName())
        || element.getClassName().startsWith(ASMBasedTaskDescriptor.Agent.class.getName())
        || element.getClassName().startsWith(FindMethodCallAnalyzer.class.getName())
        || element.getClassName().startsWith(LambdaClassLocator.class.getName())
        || element.getClassName().startsWith(LambdaMethodVisitor.class.getName())
        || element.getClassName().startsWith(SourcePointer.class.getName())
        || element.getClassName().startsWith(SyntheticLambdaAnalyzer.class.getName())
        || element.getClassName().startsWith(SyntheticLambdaAnalyzer.SyntheticLambdaMethodVisitor.class.getName())
        || element.getMethodName().startsWith("lambda$")
        || element.getClassName().contains("$$Lambda$"));
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
