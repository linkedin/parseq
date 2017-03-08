package com.linkedin.parseq;

/**
 * An API to provide description for task.
 *
 * parseq-lambda-names provide an implementation for this interface using Java SPI. Any changes made to this interface
 * might require a change to implementation in parseq-lambda-names.
 *
 * @author Siddharth Sodhani (ssodhani@linkedin.com)
 */
public interface TaskDescriptor {

  /**
   * Give class name which could correspond to generated lambda expressions etc infer appropriate description for it
   *
   * @param className
   * @return description for task
   */
  String getDescription(String className);
}
