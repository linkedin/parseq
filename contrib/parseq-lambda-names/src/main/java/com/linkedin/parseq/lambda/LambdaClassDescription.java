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
    _inferredOperationOptional = inferredOperation == null ? Optional.empty() : Optional.of(inferredOperation);
  }

  String getDescription() {
    StringBuilder builder = new StringBuilder();
    if (_inferredOperationOptional.isPresent()) {
      String desc = _inferredOperationOptional.get().toString();
      if (!desc.isEmpty()) {
        builder.append(desc).append(" ");
      }
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
