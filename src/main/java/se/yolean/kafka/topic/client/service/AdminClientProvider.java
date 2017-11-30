package se.yolean.kafka.topic.client.service;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

public class AdminClientProvider implements Provider<AdminClient> {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private String bootstrap;

  @Inject
  public AdminClientProvider(@Named("bootstrap.servers") String bootstrap) {
    this.bootstrap = bootstrap;
  }

  @Override
  public AdminClient get() {
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    log.debug("Creating AdminClient", "bootstrap", bootstrap);
    return AdminClient.create(props);
  }

}
