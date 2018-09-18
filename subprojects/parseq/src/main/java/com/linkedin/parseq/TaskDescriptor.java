package com.linkedin.parseq;

/**
 * An API to provide description for task.
 *
 * parseq-lambda-names provide an implementation for this interface using Java SPI. Any changes made to this interface
 * might require a change to implementation in parseq-lambda-names.
 *
 * The implementation doesnt need to be thread-safe.
 *
 * @author Siddharth Sodhani (ssodhani@linkedin.com)
 */
public interface TaskDescriptor {

  /**
   * Give class name which could correspond to generated lambda expressions etc infer appropriate description for it
   * If it is unable to infer description, it returns className
   *
   * @param className
   * @return description for task it can be inferred else returns className
   */
  String getDescription(String className);
}
