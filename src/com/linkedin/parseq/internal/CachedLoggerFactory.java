package com.linkedin.parseq.internal;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;


/**
 * This wrapper class caches the Logger for given logger names.
 * This is going to be the default LoggerFactory used to build Engine
 * The motivation to avoid the performance impact of log4j2's getLogger() method, which calls slow getContext() method.
 *
 * Note that as a tradeoff, LoggerContext is not supported.
 *
 * @author Zhenkai Zhu
 */
public class CachedLoggerFactory implements ILoggerFactory {
  private final ILoggerFactory _loggerFactory;
  private final ConcurrentHashMap<String, Logger> _loggers;

  public CachedLoggerFactory(ILoggerFactory loggerFactory) {
    _loggerFactory = loggerFactory;
    _loggers = new ConcurrentHashMap<>();
  }

  @Override
  public Logger getLogger(String name) {
    return _loggers.computeIfAbsent(name, _loggerFactory::getLogger);
  }
}
