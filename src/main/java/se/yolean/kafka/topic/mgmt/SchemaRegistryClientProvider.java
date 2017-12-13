package se.yolean.kafka.topic.mgmt;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;

public class SchemaRegistryClientProvider implements Provider<SchemaRegistryClient> {

  public static final int INITIAL_MAP_CAPACITY = 10;

  @Inject
  @Named("config:schemaRegistryUrl")
  private String schemaRegistryBaseUrls;

  @Override
  public SchemaRegistryClient get() {
    // are there other impls?
    return new CachedSchemaRegistryClient(schemaRegistryBaseUrls, INITIAL_MAP_CAPACITY);
  }

}
