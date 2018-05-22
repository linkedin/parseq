package com.linkedin.restli.client.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static com.linkedin.restli.client.config.RequestConfigProviderImpl.DEFAULT_TIMEOUT;

import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.restli.client.InboundRequestContext;
import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestliClientConfigBuilder;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.examples.greetings.client.AssociationsSubBuilders;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;
import com.linkedin.restli.examples.groups.client.GroupsBuilders;


public class TestRequestConfigProvider {

  @Test
  public void testFromEmptyMap() throws RequestConfigKeyParsingException {
    RequestConfigProvider provider =
        RequestConfigProvider.build(new ParSeqRestliClientConfigBuilder().build(), () -> Optional.empty());
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(DEFAULT_TIMEOUT));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new AssociationsSubBuilders().get().srcKey("a").destKey("b").id("x").build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(DEFAULT_TIMEOUT));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testFromEmptyMapOverrideDefault() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.*", 1000L);
    configBuilder.addMaxBatchSize("*.*/*.*", 4096);
    configBuilder.addBatchingEnabled("*.*/*.*", true);
    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(true));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(4096));

    rc = provider.apply(new AssociationsSubBuilders().get().srcKey("a").destKey("b").id("x").build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(true));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(4096));
  }

  @Test
  public void testOutboundOp() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(DEFAULT_TIMEOUT));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new AssociationsSubBuilders().get().srcKey("a").destKey("b").id("x").build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testOutboundName() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/greetings.*", 1000L);
    configBuilder.addTimeoutMs("*.*/associations:foo.*", 1001L);
    configBuilder.addTimeoutMs("*.*/associations.*", 1000L);
    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GroupsBuilders().get().id(10).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(DEFAULT_TIMEOUT));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new AssociationsSubBuilders().get().srcKey("a").destKey("b").id("x").build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesOutboundNameSubResource() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/associations:foo.*", 1000L);
    configBuilder.addTimeoutMs("*.*/associations.*", 1001L);
    configBuilder.addTimeoutMs("*.*/associations:associationsSub.*", 1002L);
    configBuilder.addTimeoutMs("*.*/associations-prod-lsg1:associationsSub.*", 1003L);
    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    RequestConfig rc = provider.apply(new AssociationsSubBuilders().get().srcKey("a").destKey("b").id("x").build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1002L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    // multi-colo call
    rc = provider.apply(new AssociationsSubBuilders("associations" + "-prod-lsg1").get()
        .srcKey("a").destKey("b").id("x").build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1003L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testSubResourceNoMultiColo() {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/associations-prod-lsg1:associationsSub-prod-lsg1.*", 1003L);
    try {
      RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    } catch (Throwable e) {
      assertTrue(e instanceof RuntimeException && e.getCause() instanceof RequestConfigKeyParsingException);
    }
  }

  @Test
  public void testTimeoutForGetManyConfigs() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1000L);
    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1000L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(DEFAULT_TIMEOUT));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundAndOutboundMatch() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1000L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/*.GET", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/greetings.GET", 100L);
    configBuilder.addTimeoutMs("greetings.GET/greetings-prod-lsg1.GET", 200L);
    configBuilder.addTimeoutMs("*.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.*/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("*.GET/greetings.DELETE", 1000L);
    configBuilder.addTimeoutMs("greetings.GET/greetings.DELETE", 500L);

    RequestConfigProvider provider =
        RequestConfigProvider.build(configBuilder.build(), requestContextFinder("greetings",
            ResourceMethod.GET.toString().toUpperCase(), Optional.empty(), Optional.empty()));
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(100L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    // multi-colo call
    rc = provider.apply(new GreetingsBuilders("greetings" + "-prod-lsg1").get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(200L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));

    rc = provider.apply(new GreetingsBuilders().delete().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(500L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundSubresourceAndOutboundMatch() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
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

    RequestConfigProvider provider =
        RequestConfigProvider.build(configBuilder.build(), requestContextFinder("greetings:associationsSub",
            ResourceMethod.GET.toString().toUpperCase(), Optional.empty(), Optional.empty()));
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
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
  public void testPrioritiesWithInboundAndOutboundMatchSubresource() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1001L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1002L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1003L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1004L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1005L);
    configBuilder.addTimeoutMs("*.GET/greetings.GET", 1006L);
    configBuilder.addTimeoutMs("greetings.GET/*.GET", 1007L);
    configBuilder.addTimeoutMs("greetings:associationsSub.GET/greetings.GET", 1008L);

    RequestConfigProvider provider =
        RequestConfigProvider.build(configBuilder.build(), requestContextFinder("greetings",
            ResourceMethod.GET.toString().toUpperCase(), Optional.empty(), Optional.empty()));
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1006L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundAndOutboundMatchSubresource2() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/*.GET", 1000L);
    configBuilder.addTimeoutMs("x.GET/*.GET", 1001L);
    configBuilder.addTimeoutMs("y.GET/x1.GET", 1002L);
    configBuilder.addTimeoutMs("y.GET/x2.GET", 1003L);
    configBuilder.addTimeoutMs("*.GET/x.GET", 1004L);
    configBuilder.addTimeoutMs("*.GET/x2.GET", 1005L);
    configBuilder.addTimeoutMs("*.GET/greetings.GET", 1006L);
    configBuilder.addTimeoutMs("greetings.GET/*.GET", 1007L);
    configBuilder.addTimeoutMs("greetings:associationsSub.GET/greetings.GET", 1008L);

    RequestConfigProvider provider =
        RequestConfigProvider.build(configBuilder.build(), requestContextFinder("greetings:associationsSub",
            ResourceMethod.GET.toString().toUpperCase(), Optional.empty(), Optional.empty()));
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(1008L));
    assertEquals(rc.isBatchingEnabled().getValue(), Boolean.valueOf(false));
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(1024));
  }

  @Test
  public void testPrioritiesWithInboundFinderAndOutboundMatch() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
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

    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(),
        requestContextFinder("greetings", "FINDER", Optional.of("findAll"), Optional.empty()));
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
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
  public void testPrioritiesWithHttpInboundAndOutboundMatch() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
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

    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(),
        requestContextFinder("greetings", "POST", Optional.empty(), Optional.empty()));
    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
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

  /**
   * Default values are specified only at the top of the config hierarchy.
   * It means that when RequestConfigTree is traversing configuration it is
   * not guaranteed that it will return a full Optional for parameter Optional.empty.
   * It is guaranteed only at the root level.
   * This unit test tests case when while traversing configuration tree there is no match
   * in the middle of the hierarchy. More specifically, the outbound name will match but then
   * operation name will not match: config tree contains only entry for DELETE but test is
   * trying to find entry for GET.
   */
  @Test
  public void testNoMatchInTheMiddleOfHierarchy() throws RequestConfigKeyParsingException {
    ParSeqRestliClientConfigBuilder configBuilder = new ParSeqRestliClientConfigBuilder();
    configBuilder.addTimeoutMs("*.*/greetings.DELETE", 1000L);

    RequestConfigProvider provider = RequestConfigProvider.build(configBuilder.build(), () -> Optional.empty());

    RequestConfig rc = provider.apply(new GreetingsBuilders().get().id(0L).build());
    assertNotNull(rc);
    assertEquals(rc.getTimeoutMs().getValue(), Long.valueOf(RequestConfigProviderImpl.DEFAULT_TIMEOUT));
    assertEquals(rc.isBatchingEnabled().getValue(), RequestConfigProviderImpl.DEFAULT_BATCHING_ENABLED);
    assertEquals(rc.getMaxBatchSize().getValue(), Integer.valueOf(RequestConfigProviderImpl.DEFAULT_MAX_BATCH_SIZE));
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
