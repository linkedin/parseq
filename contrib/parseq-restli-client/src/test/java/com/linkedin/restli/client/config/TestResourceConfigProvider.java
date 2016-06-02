package com.linkedin.restli.client.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.restli.client.InboundRequestContext;
import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;
import com.linkedin.restli.examples.groups.client.GroupsBuilders;

public class TestResourceConfigProvider {

  @Test
  public void testFromEmptyMap() throws ResourceConfigKeyParsingException {
    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(Collections.emptyMap(), () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testFromEmptyMapOverrideDefault() throws ResourceConfigKeyParsingException {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/*.*", 1000L);
    addProperty(config, "maxBatchSize", "*.*/*.*", 4096);
    addProperty(config, "batchingEnabled", "*.*/*.*", true);
    addProperty(config, "batchingDryRun", "*.*/*.*", true);
    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(config, () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(true));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(true));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(4096));
  }

  @Test
  public void testOutboundOp() throws ResourceConfigKeyParsingException {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/*.GET", 1000L);
    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(config, () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testOutboundName() throws ResourceConfigKeyParsingException {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/greetings.*", 1000L);
    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(config, () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GroupsBuilders().get().id(10).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testTimeoutForGetManyConfigs() throws ResourceConfigKeyParsingException {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/*.GET", 1000L);
    addProperty(config, "timeoutMs", "x.GET/*.GET", 1000L);
    addProperty(config, "timeoutMs", "y.GET/x1.GET", 1000L);
    addProperty(config, "timeoutMs", "y.GET/x2.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/x.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/x2.GET", 1000L);
    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(config, () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundAndOutboundMatch() throws ResourceConfigKeyParsingException {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/*.GET", 1000L);
    addProperty(config, "timeoutMs", "x.GET/*.GET", 1000L);
    addProperty(config, "timeoutMs", "y.GET/x1.GET", 1000L);
    addProperty(config, "timeoutMs", "y.GET/x2.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/x.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/x2.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/greetings.GET", 1000L);
    addProperty(config, "timeoutMs", "greetings.GET/*.GET", 1000L);
    addProperty(config, "timeoutMs", "greetings.GET/greetings.GET", 100L);
    addProperty(config, "timeoutMs", "*.*/greetings.DELETE", 1000L);
    addProperty(config, "timeoutMs", "greetings.*/greetings.DELETE", 1000L);
    addProperty(config, "timeoutMs", "*.GET/greetings.DELETE", 1000L);
    addProperty(config, "timeoutMs", "greetings.GET/greetings.DELETE", 500L);

    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(config,
        requestContextFinder("greetings", ResourceMethod.GET.toString().toUpperCase(), Optional.empty(), Optional.empty()));
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(100L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(500L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithHttpInboundAndOutboundMatch() throws ResourceConfigKeyParsingException {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/*.GET", 1000L);
    addProperty(config, "timeoutMs", "x.GET/*.GET", 1000L);
    addProperty(config, "timeoutMs", "y.GET/x1.GET", 1000L);
    addProperty(config, "timeoutMs", "y.GET/x2.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/x.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/x2.GET", 1000L);
    addProperty(config, "timeoutMs", "*.GET/greetings.GET", 1000L);
    addProperty(config, "timeoutMs", "greetings.GET/*.GET", 1000L);
    addProperty(config, "timeoutMs", "greetings.POST/greetings.GET", 100L);
    addProperty(config, "timeoutMs", "*.*/greetings.DELETE", 1000L);
    addProperty(config, "timeoutMs", "greetings.*/greetings.DELETE", 1000L);
    addProperty(config, "timeoutMs", "*.GET/greetings.DELETE", 1000L);
    addProperty(config, "timeoutMs", "greetings.POST/greetings.DELETE", 500L);

    ResourceConfigProvider provider = ResourceConfigProvider.fromMap(config,
        requestContextFinder("greetings", "POST", Optional.empty(), Optional.empty()));
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(100L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(500L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.isBatchingDryRun().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  private InboundRequestContextFinder requestContextFinder(String name, String method, Optional<String> finderName, Optional<String> actionName) {
    return new InboundRequestContextFinder() {
      @Override
      public Optional<InboundRequestContext> find() {
        return Optional.of(new InboundRequestContext() {

          @Override
          public String getName() {
            return name;
          }

          @Override
          public String getMethod() {
            return method;
          }

          @Override
          public Optional<String> getFinderName() {
            return finderName;
          }

          @Override
          public Optional<String> getActionName() {
            return actionName;
          }
        });
      }
    };
  }

  private static <T> void addProperty(Map<String, Map<String, Object>> config, String property, String key, T value) {
    Map<String, Object> map = config.computeIfAbsent(property, k -> new HashMap<>());
    map.put(key, value);
  }

}
