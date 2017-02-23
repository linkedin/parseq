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
   * Given class which could correspond to generated lambda expressions etc infer appropriate description for it
   *
   * @param clazz
   * @return description for task
   */
  String getDescription(Class<?> clazz);
}
