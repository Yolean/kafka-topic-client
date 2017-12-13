package se.yolean.kafka.topic.client.retryable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.client.config.AdminClientProvider;
import se.yolean.kafka.topic.client.config.ItestProps;

public class BrokerProbeIntegrationTest {

  @Test
  public void test() throws Exception {
    Injector conf = Guice.createInjector(ItestProps.DOCKER_COMPOSE, new AbstractModule() {
      protected void configure() {
        bind(AdminClient.class).toProvider(AdminClientProvider.class);
      }
    });
    BrokerProbe probe = conf.getInstance(BrokerProbe.class);
    probe.call();
  }

  @Test
  public void testTimeoutDescribeNodes() {
    Injector conf = Guice.createInjector(ItestProps.DOCKER_COMPOSE.override("brokers.describe.timeout.ms", 1),
        new AbstractModule() {
      protected void configure() {
        bind(AdminClient.class).toProvider(AdminClientProvider.class);
      }
    });
    BrokerProbe.timeouts.clear();
    try {
      conf.getInstance(BrokerProbe.class).call();
      fail("Should have thrown exception");
    } catch (org.apache.kafka.common.errors.TimeoutException e) {
      // ok, we don't wrap this unless we can also document a specific behavior
    } catch (Exception e) {
      fail("Should have thrown a specific exception");
    }
    assertEquals(1, BrokerProbe.timeouts.labels("broker_probe").get(), 0.1);
  }

  @Test
  public void testTimeoutDescribeNodesGet() {
    Injector conf = Guice.createInjector(ItestProps.DOCKER_COMPOSE
        .override("brokers.describe.get.timeout.ms", 1),
        new AbstractModule() {
      protected void configure() {
        bind(AdminClient.class).toProvider(AdminClientProvider.class);
      }
    });
    BrokerProbe.timeouts.clear();
    try {
      conf.getInstance(BrokerProbe.class).call();
      fail("Should have thrown exception");
    } catch (java.util.concurrent.TimeoutException e) {
      // ok, we don't wrap this unless we can also document a specific behavior
    } catch (Exception e) {
      fail("Should have thrown a specific exception");
    }
    assertEquals(1, BrokerProbe.timeouts.labels("broker_probe").get(), 0.1);
  }

  @Test
  public void testBrokersNotEnough() {
    Injector conf = Guice.createInjector(ItestProps.DOCKER_COMPOSE
        .override("brokers.describe.available.min", 9),
        new AbstractModule() {
      protected void configure() {
        bind(AdminClient.class).toProvider(AdminClientProvider.class);
      }
    });
    BrokerProbe.timeouts.clear();
    try {
      conf.getInstance(BrokerProbe.class).call();
      fail("Should have thrown exception");
    } catch (NotEnoughBrokersException e) {
      // good
    } catch (Exception e) {
      e.printStackTrace();
      fail("Should have thrown a specific exception");
    }
    assertEquals(0, BrokerProbe.timeouts.labels("broker_probe").get(), 0.1);
  }

}
