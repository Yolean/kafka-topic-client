package se.yolean.kafka.topic.client.cli;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;

import se.yolean.kafka.topic.client.config.ConfigModule;
import se.yolean.kafka.topic.client.config.ManagerInitModule;
import se.yolean.kafka.topic.client.config.MetricsModule;
import se.yolean.kafka.topic.client.retryable.BrokerProbe;
import se.yolean.kafka.topic.client.retryable.RestProxySetup;
import se.yolean.kafka.topic.client.retryable.SchemaRegistrySetup;
import se.yolean.kafka.topic.manager.configure.TopicDeclarationsPollModule;
import se.yolean.kafka.topic.manager.tt.TopicsTopicWatcher;

public class ManagedTopicsService implements Runnable {

  public final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private final Injector serviceContext;

  public ManagedTopicsService(Properties config) {
    serviceContext = Guice.createInjector(
        // ny async or retry behavior now, so configure long timeouts instead
        //new ConcurrencyModule(),
        new ConfigModule(config)
        );
  }

  public void start() {
    log.info("Starting Topic Manager Service without concurrency", "hostname", getHostname());
    run();
  }

  public void stop() {
    log.warn("TODO shutdown not implemented. Send termination signals or configure topic.declarations.consumer.polls.max.");
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

    BrokerProbe brokerProbe = initContext.getInstance(BrokerProbe.class);
    BrokerProbe.KafkaStatus status;
    try {
      status = brokerProbe.call();
    } catch (Exception e) {
      throw new RuntimeException("unhandled", e);
    }

    SchemaRegistrySetup schemaRegistry = initContext.getInstance(SchemaRegistrySetup.class);
    SchemaRegistrySetup.AdminSchemaStatus schemas;
    try {
      schemas = schemaRegistry.call();
    } catch (Exception e) {
      throw new RuntimeException("unhandled", e);
    }

    log.info("Both kafka and schema registry is ok, now create REST producer for declarations");
    RestProxySetup restProxy = initContext.getInstance(RestProxySetup.class);

    RestProxySetup.EndpointsStatus rest;
    try {
      rest = restProxy.call();
    } catch (Exception e) {
      throw new RuntimeException("unhandled", e);
    }

    log.info("REST endpoints also OK, let's start consuming topic declarations");

    Injector managerContext = initContext.createChildInjector(
        new TopicDeclarationsPollModule(status, schemas, rest));


    TopicsTopicWatcher watch = managerContext.getInstance(TopicsTopicWatcher.class);
    log.info("Handing control over to topic declarations poll loop", "impl", watch.getClass());
    watch.run();
  }

  String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new RuntimeException("Failed to get hostname", e);
    }
  }

}
