package se.yolean.kafka.topic.client.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.nurkiewicz.asyncretry.RetryExecutor;

public class ManagerConfigModule extends AbstractModule {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private Properties config;

  public ManagerConfigModule(Properties config) {
    this.config = config;
    logConfigValues();
  }

  void logConfigValues() {
    StringWriter writer = new StringWriter();
    config.list(new PrintWriter(writer));
    log.info("Topic Manager config: " + writer.getBuffer().toString());
  }

  @Override
  protected void configure() {
    Names.bindProperties(super.binder(), this.config);

    bind(ScheduledExecutorService.class).toProvider(ExecutorServiceProvider.class);
    bind(RetryExecutor.class).toProvider(ExecutorRetryProviderForInit.class);
  }

}
