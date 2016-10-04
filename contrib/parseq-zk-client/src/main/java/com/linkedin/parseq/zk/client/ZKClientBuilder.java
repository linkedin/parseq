package com.linkedin.parseq.zk.client;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.internal.ArgumentUtil;


/**
 * @author Ang Xu
 */
public class ZKClientBuilder {

  private String _connectionString;
  private int _sessionTimeout;
  private Engine _engine;

  public ZKClientBuilder setConnectionString(String connectionString) {
    ArgumentUtil.requireNotNull(connectionString, "zk connection string");
    _connectionString = connectionString;
    return this;
  }

  public ZKClientBuilder setSessionTimeout(int sessionTimeout) {
    ArgumentUtil.requirePositive(sessionTimeout, "session timeout");
    _sessionTimeout = sessionTimeout;
    return this;
  }

  public ZKClientBuilder setEngine(Engine engine) {
    ArgumentUtil.requireNotNull(engine, "engine");
    _engine = engine;
    return this;
  }

  public ZKClient build() {
    return new ZKClientImpl(_connectionString, _sessionTimeout, _engine);
  }
}
