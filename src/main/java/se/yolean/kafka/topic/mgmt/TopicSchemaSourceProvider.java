package se.yolean.kafka.topic.mgmt;

import javax.inject.Provider;

import se.yolean.kafka.topic.declaration.Topic;

public class TopicSchemaSourceProvider implements Provider<String> {

  @Override
  public String get() {
    return Topic.SCHEMA$.toString();
  }

}
