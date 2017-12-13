package se.yolean.kafka.topic.client.config;

import org.apache.kafka.clients.admin.AdminClient;

import com.google.inject.AbstractModule;

public class ManagerInitModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AdminClient.class).toProvider(AdminClientProvider.class);
  }

}
