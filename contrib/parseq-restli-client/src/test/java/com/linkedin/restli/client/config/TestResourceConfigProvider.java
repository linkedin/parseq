package com.linkedin.restli.client.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.restli.client.InboundRequestContext;
import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestClientConfigBuilder;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;
import com.linkedin.restli.examples.groups.client.GroupsBuilders;


public class TestResourceConfigProvider {

  @Test
  public void testFromEmptyMap() throws ResourceConfigKeyParsingException {
    ResourceConfigProvider provider =
        ResourceConfigProvider.build(new ParSeqRestClientConfigBuilder().build(), () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testFromEmptyMapOverrideDefault() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.*", 1000L);
    configBuilder.addMaxBatchSize("*.*/*.*", 4096);
    configBuilder.addBatchingEnabled("*.*/*.*", true);
    ResourceConfigProvider provider = ResourceConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(true));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(4096));
  }

  @Test
  public void testOutboundOp() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    ResourceConfigProvider provider = ResourceConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testOutboundName() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/greetings.*", 1000L);
    ResourceConfigProvider provider = ResourceConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GroupsBuilders().get().id(10).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testTimeoutForGetManyConfigs() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1000L);
    ResourceConfigProvider provider = ResourceConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(10000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundAndOutboundMatch() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/greetings.GET", 100L);
    configBuilder.addTimeoutMs("*.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/greetings.DELETE", 500L);

    ResourceConfigProvider provider =
        ResourceConfigProvider.build(configBuilder.build(), requestContextFinder("greetings",
            ResourceMethod.GET.toString().toUpperCase(), Optional.empty(), Optional.empty()));
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(100L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(500L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundFinderAndOutboundMatch() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/greetings.GET", 100L);
    configBuilder.addTimeoutMs("*.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/greetings.DELETE", 500L);
    configBuilder.addTimeoutMs("greetings.FINDER-*/greetings.GET", 500L);
    configBuilder.addTimeoutMs("greetings.FINDER-*/greetings.DELETE", 500L);
    configBuilder.addTimeoutMs("greetings.FINDER-foobar/greetings.GET", 500L);
    configBuilder.addTimeoutMs("greetings.FINDER-foobar/greetings.DELETE", 500L);
    configBuilder.addTimeoutMs("greetings.FINDER-findAll/greetings.GET", 400L);
    configBuilder.addTimeoutMs("greetings.FINDER-findAll/greetings.DELETE", 300L);

    ResourceConfigProvider provider = ResourceConfigProvider.build(configBuilder.build(),
        requestContextFinder("greetings", "FINDER", Optional.of("findAll"), Optional.empty()));
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(400L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(300L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithHttpInboundAndOutboundMatch() throws ResourceConfigKeyParsingException {
    ParSeqRestClientConfigBuilder configBuilder = new ParSeqRestClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.POST/greetings.GET", 100L);
    configBuilder.addTimeoutMs("*.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.POST/greetings.DELETE", 500L);

    ResourceConfigProvider provider = ResourceConfigProvider.build(configBuilder.build(),
        requestContextFinder("greetings", "POST", Optional.empty(), Optional.empty()));
    ResourceConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(100L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(500L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  private InboundRequestContextFinder requestContextFinder(String name, String method, Optional<String> finderName,
      Optional<String> actionName) {
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
}
