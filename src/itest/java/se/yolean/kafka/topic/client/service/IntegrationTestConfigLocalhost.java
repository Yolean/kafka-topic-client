package se.yolean.kafka.topic.client.service;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class IntegrationTestConfigLocalhost extends AbstractModule {

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("config:bootstrap")).toInstance("localhost:9092");

    bind(String.class).annotatedWith(Names.named("config:adminTopic")).toInstance("_topic_declarations");

    bind(Integer.class).annotatedWith(Names.named("config:adminInitTimeoutMs")).toInstance(1000);

    bind(Integer.class).annotatedWith(Names.named("config:adminTopicDesiredReplicationFactor")).toInstance(1);

    bind(Properties.class).annotatedWith(Names.named("admin")).toProvider(AdminClientPropsProvider.class);
  }

}
