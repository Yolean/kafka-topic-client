package se.yolean.kafka.topic.client.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigModule extends AbstractModule {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private Properties config;

  public ConfigModule(Properties config) {
    this.config = config;
    logConfigValues();
  }

  void logConfigValues() {
    StringWriter writer = new StringWriter();
    config.list(new PrintWriter(writer));
    log.info("Instance config: " + writer.getBuffer().toString());
  }

  @Override
  protected void configure() {
    Names.bindProperties(super.binder(), this.config);
  }

}
