package se.yolean.kafka.topic.client.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.databind.util.Annotations;
import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nurkiewicz.asyncretry.RetryExecutor;

import se.yolean.kafka.topic.client.config.ManagerConfigModule;
import se.yolean.kafka.topic.client.config.MetricsModule;
import se.yolean.kafka.topic.client.retryable.RestProxySetup;
import se.yolean.kafka.topic.client.retryable.SchemaRegistrySetup;
import se.yolean.kafka.topic.client.config.ExecutorRetryProviderForInit;

public class ManagedTopicsService {

  public final ILogger log = SLoggerFactory.getLogger(this.getClass());

  public ManagedTopicsService(Properties config) {
    log.info("Starting Topic Manager Service", "hostname", getHostname());

    Injector initContext = Guice.createInjector(
        new ManagerConfigModule(config),
        new MetricsModule()
        );

    MetricsModule.Exporter exporter = initContext.getInstance(MetricsModule.Exporter.class);
    log.info("Metrics exporter", "status", exporter.getStatus(), "port", exporter.getHttpPort());



    SchemaRegistrySetup shemaRegistry = initContext.getInstance(SchemaRegistrySetup.class);

    RestProxySetup restProxy = initContext.getInstance(RestProxySetup.class);

    initContext.getInstance(ExecutorRetryProviderForInit.class);
    RetryExecutor ex = null;
    ex.getWithRetry(() -> new String());
  }

  String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new RuntimeException("Failed to get hostname", e);
    }
  }

}
