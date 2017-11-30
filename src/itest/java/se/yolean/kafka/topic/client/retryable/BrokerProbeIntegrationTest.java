package se.yolean.kafka.topic.client.retryable;

import org.apache.kafka.clients.admin.AdminClient;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.client.config.ItestProps;
import se.yolean.kafka.topic.client.service.AdminClientProvider;

public class BrokerProbeIntegrationTest {

  @Test
  public void test() throws Exception {
    Injector conf = Guice.createInjector(ItestProps.DOCKER_COMPOSE, new AbstractModule() {
      @Override
      protected void configure() {
        bind(AdminClient.class).toProvider(AdminClientProvider.class);
      }
    });
    BrokerProbe probe = conf.getInstance(BrokerProbe.class);
    probe.call();
  }

}
