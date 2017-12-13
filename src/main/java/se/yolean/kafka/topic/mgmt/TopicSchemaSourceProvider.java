package se.yolean.kafka.topic.mgmt;

import javax.inject.Provider;

import org.apache.avro.Schema;

import se.yolean.kafka.topic.declaration.Topic;

public class TopicSchemaSourceProvider implements Provider<Schema> {

  @Override
  public Schema get() {
    return Topic.SCHEMA$;
  }

}
