package com.linkedin.restli.client;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.RequestConfigProvider;


/**
 * A builder to construct {@link ParSeqRestClient} based on provided configurations.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 * @author Min Chen (mnchen@linkedin.com)
 */
public class ParSeqRestliClientBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParSeqRestliClientBuilder.class);

  private static final String DEFAULT_CONFIG = "default";

  private Client _client;
  private ParSeqRestliClientConfig _config;
  Map<String, ParSeqRestliClientConfig> _configs;
  ParSeqRestliClientConfigChooser _configChooser;
  private boolean _d2RequestTimeoutEnabled = false;

  private BatchingSupport _batchingSupport;
  private InboundRequestContextFinder _inboundRequestContextFinder;
  private Function<Request<?>, RequestContext> _requestContextProvider;

  /**
   * This method may throw RuntimeException e.g. when there is a problem with configuration.
   *
   * @throws RuntimeException e.g. when there is a problem with configuration
   * @return instance of ParSeqRestClient
   */
  public ParSeqRestClient build() {

    if (_inboundRequestContextFinder == null) {
      LOGGER.debug("InboundRequestContextFinder not specified, using default one");
    }
    InboundRequestContextFinder inboundRequestContextFinder = _inboundRequestContextFinder == null ?
        () -> Optional.empty() : _inboundRequestContextFinder;

    if (_config != null) {
      LOGGER.debug("One config specified");
      _configs = Collections.singletonMap(DEFAULT_CONFIG, _config);
      _configChooser = (irc, r) -> DEFAULT_CONFIG;
    } else if (_configs == null) {
      throw new IllegalStateException("One type of config has to be specified using either setConfig() or setMultipleConfigs().");
    } else {
      LOGGER.debug("Multiple configs specified");
    }

    RequestConfigProvider configProvider = new MultipleRequestConfigProvider(_configs, _configChooser, inboundRequestContextFinder);
    Function<Request<?>, RequestContext> requestContextProvider = (_requestContextProvider == null) ?
        request -> new RequestContext() :
        _requestContextProvider;

    ParSeqRestClient parseqClient = new ParSeqRestClient(_client, configProvider, requestContextProvider, _d2RequestTimeoutEnabled);
    if (_batchingSupport != null) {
      LOGGER.debug("Found batching support");
      _batchingSupport.registerStrategy(parseqClient);
    } else {
      LOGGER.debug("Did not find batching support");
    }
    return parseqClient;
  }

  /**
   * Gets the underlying Rest.li client implementation.
   *
   * @deprecated Calling #get in a builder is an anti-pattern
   * @return The underlying Rest.li client
   */
  @Deprecated
  public Client getRestClient() {
    return _client;
  }

  public ParSeqRestliClientBuilder setBatchingSupport(BatchingSupport batchingSupport) {
    _batchingSupport = batchingSupport;
    return this;
  }

  /**
   * Sets the underlying Rest.li client implementation.
   *
   * @param client The underlying Rest.li client
   * @deprecated Use #setClient instead
   * @return The builder itself
   */
  @Deprecated
  public ParSeqRestliClientBuilder setRestClient(RestClient client) {
    ArgumentUtil.requireNotNull(client, "client");
    _client = client;
    return this;
  }

  public ParSeqRestliClientBuilder setClient(Client client) {
    ArgumentUtil.requireNotNull(client, "client");
    _client = client;
    return this;
  }

  public ParSeqRestliClientBuilder setRequestContextProvider(Function<Request<?>, RequestContext> requestContextProvider) {
    ArgumentUtil.requireNotNull(requestContextProvider, "requestContextProvider");
    _requestContextProvider = requestContextProvider;
    return this;
  }

  public ParSeqRestliClientBuilder setConfig(ParSeqRestliClientConfig config) {
    ArgumentUtil.requireNotNull(config, "config");
    if (_configs != null) {
      throw new IllegalArgumentException("setMultipleConfigs() has already been called. Only one type of config can be specified using either setConfig() or setMultipleConfigs() but not both.");
    }
    _config = config;
    return this;
  }

  public ParSeqRestliClientBuilder setMultipleConfigs(Map<String, ParSeqRestliClientConfig> configs,
      ParSeqRestliClientConfigChooser chooser) {
    ArgumentUtil.requireNotNull(configs, "configs");
    ArgumentUtil.requireNotNull(chooser, "chooser");
    if (_configs != null) {
      throw new IllegalArgumentException("setConfig() has already been called. Only one type of config can be specified using either setConfig() or setMultipleConfigs() but not both.");
    }
    _configs = configs;
    _configChooser = chooser;
    return this;
  }

  public ParSeqRestliClientBuilder setInboundRequestContextFinder(InboundRequestContextFinder inboundRequestContextFinder) {
    ArgumentUtil.requireNotNull(inboundRequestContextFinder, "inboundRequestContextFinder");
    _inboundRequestContextFinder = inboundRequestContextFinder;
    return this;
  }

  /**
   * Enables or disables d2 per-request timeout.
   *
   * Once enabled, the timeout used in Parseq can be passed down into rest.li R2D2 layer
   * and free up memory sooner than currently possible. For example, if the server defined
   * requestTimeout is 10s, but the client only wants to wait 250ms, parseq rest client
   * could tell r2d2 to trigger the user callback after 250 ms instead of 10s, which would
   * help us handle the case of slow downstream services, which today cause memory problems
   * for high qps upstream services because more objects are being held longer in memory.
   *
   * @param enabled true if this feature is enabled.
   */
  public ParSeqRestliClientBuilder setD2RequestTimeoutEnabled(boolean enabled) {
    _d2RequestTimeoutEnabled = enabled;
    return this;
  }
}
