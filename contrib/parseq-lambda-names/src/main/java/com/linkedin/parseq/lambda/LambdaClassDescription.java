package com.linkedin.parseq.lambda;

import java.util.Optional;


class LambdaClassDescription {

  private final String _className;
  private final SourcePointer _sourcePointer;
  private final Optional<InferredOperation> _inferredOperationOptional;

  LambdaClassDescription(String className, SourcePointer sourcePointer,
                                InferredOperation inferredOperation) {
    _className = className;
    _sourcePointer = sourcePointer;
    _inferredOperationOptional = Optional.of(inferredOperation);
  }

  String getDescription() {
    StringBuilder builder = new StringBuilder();
    if (_inferredOperationOptional.isPresent()) {
      builder.append(_inferredOperationOptional.get()).append(" ");
    } else {
      //TODO: the best way to log this so that these could be looked into, may be log source pointer atleast to debug
    }

    builder.append(_sourcePointer);
    return builder.toString();
  }

  String getClassName() {
    return _className;
  }

  @Override
  public String toString() {
    return getDescription();
  }
}
