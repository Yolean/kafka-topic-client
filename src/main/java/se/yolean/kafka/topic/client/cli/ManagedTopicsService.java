package se.yolean.kafka.topic.client.cli;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nurkiewicz.asyncretry.RetryExecutor;

import se.yolean.kafka.topic.client.config.ConcurrencyModule;
import se.yolean.kafka.topic.client.config.ConfigModule;
import se.yolean.kafka.topic.client.config.ManagerInitModule;
import se.yolean.kafka.topic.client.config.MetricsModule;
import se.yolean.kafka.topic.client.retryable.BrokerProbe;
import se.yolean.kafka.topic.client.retryable.BrokerProbe.KafkaStatus;
import se.yolean.kafka.topic.client.retryable.RestProxySetup;
import se.yolean.kafka.topic.client.retryable.RestProxySetup.EndpointsStatus;
import se.yolean.kafka.topic.client.retryable.SchemaRegistrySetup;
import se.yolean.kafka.topic.client.retryable.SchemaRegistrySetup.AdminSchemaStatus;

public class ManagedTopicsService implements Runnable {

  public final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private final Injector serviceContext;

  public ManagedTopicsService(Properties config) {
    serviceContext = Guice.createInjector(new ConfigModule(config), new ConcurrencyModule());
  }

  public void start() {
    //log.info("Starting Topic Manager Service", "hostname", getHostname());
    run();
  }

  public void stop() {
    log.warn("TODO shutdown not implemented");
  }

  @Override
  public void run() {
    log.info("Running Topic Manager Service");

    Injector initContext = serviceContext.createChildInjector(
        new ManagerInitModule(),
        new MetricsModule()
        );

    MetricsModule.Exporter exporter = initContext.getInstance(MetricsModule.Exporter.class);
    log.info("Metrics exporter", "status", exporter.getStatus(), "port", exporter.getHttpPort());

    final RetryExecutor tasks = initContext.getInstance(RetryExecutor.class);

    BrokerProbe brokerProbe = initContext.getInstance(BrokerProbe.class);

    // How to execute a task depends on concurrency ambitions,
    // with plain Kafka API impls actually more suitable for a dedicated thread
    // and long configured timeouts in this service.
    // On the other hand, short timeouts (aborting Kafka clients' own retry+backoff)
    // enables concurrency with other tasks such as REST-based
    CompletableFuture<KafkaStatus> brokers = tasks.getWithRetry(brokerProbe);

    SchemaRegistrySetup schemaRegistry = initContext.getInstance(SchemaRegistrySetup.class);
    CompletableFuture<AdminSchemaStatus> schemas = tasks.getWithRetry(schemaRegistry);

    brokers.thenAcceptBoth(schemas, (KafkaStatus s, AdminSchemaStatus i) -> {

      log.info("Both kafka and schema registry is ok, now create REST producer for declarations");
      RestProxySetup restProxy = initContext.getInstance(RestProxySetup.class);

      CompletableFuture<EndpointsStatus> rest = tasks.getWithRetry(restProxy);
      rest.thenAccept(endpoints -> {
        log.info("REST endpoints also OK, let's start consuming topic declarations");
        log.warn("Big fat TODO");
      });

    });

    while (true) {
      // we need to wait for the management loop here, but it can't start until the above has completed
      log.debug("Somewhere here we'll be repeating the topic management loop");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        log.info("Exiting");
      }
    }
  }

  String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new RuntimeException("Failed to get hostname", e);
    }
  }

}
