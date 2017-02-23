package com.linkedin.parseq;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class TaskDescriptorFactory {

  static final Logger LOGGER = LoggerFactory.getLogger(TaskDescriptorFactory.class);

  private static ServiceLoader<TaskDescriptor> _loader;
  private static TaskDescriptor _identifier;

  static {
    try {
      _loader = ServiceLoader.load(TaskDescriptor.class);
    } catch (SecurityException e) {
      LOGGER.error("Unable to get ContextClassLoader for loading providers of TaskDescriptor", e.getMessage());
    } catch (RuntimeException e) {
      //intentionally capturing RuntimeException
      LOGGER.error("Unable to load providers of TaskDescriptor", e.getMessage());
    }
  }

  private TaskDescriptorFactory() {
  }

  static synchronized TaskDescriptor getTaskDescriptor() {
    if (_identifier != null) {
      return _identifier;
    }

    try {
      Iterator<TaskDescriptor> identifiers = _loader.iterator();
      if (identifiers.hasNext()) {
        _identifier = identifiers.next();

        if (identifiers.hasNext()) {
          LOGGER.info("Found more than one provider for TaskDescriptor, picked first one");
        }
      } else {
        LOGGER.error("No provider found for TaskDescriptor, falling back to DefaultTaskDescriptor");
        _identifier = getDefaultTaskDescriptor();
      }
    } catch (ServiceConfigurationError e) {
      LOGGER.error("Unable to load provider for TaskDescriptor, falling back to DefaultTaskDescriptor", e.getMessage());
      _identifier = getDefaultTaskDescriptor();
    }

    return _identifier;
  }

  private static TaskDescriptor getDefaultTaskDescriptor() {
    return new DefaultTaskDescriptor();
  }

  static class DefaultTaskDescriptor implements TaskDescriptor {

    DefaultTaskDescriptor() {
    }

    @Override
    public String getDescription(Class<?> clazz) {
      return clazz.getName();
    }
  }
}
