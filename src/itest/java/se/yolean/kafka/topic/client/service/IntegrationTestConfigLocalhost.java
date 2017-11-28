package se.yolean.kafka.topic.client.service;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class IntegrationTestConfigLocalhost extends AbstractModule {

  public static final int KAFKA_LISTENER_PORT = 9094;

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("config:bootstrap")).toInstance("localhost:" + KAFKA_LISTENER_PORT);

    bind(String.class).annotatedWith(Names.named("config:adminTopic")).toInstance("_topic_declarations");

    bind(Integer.class).annotatedWith(Names.named("config:adminInitTimeoutMs")).toInstance(1000);

    bind(Integer.class).annotatedWith(Names.named("config:adminTopicDesiredReplicationFactor")).toInstance(1);

    bind(Properties.class).annotatedWith(Names.named("admin")).toProvider(AdminClientPropsProvider.class);
  }

}
