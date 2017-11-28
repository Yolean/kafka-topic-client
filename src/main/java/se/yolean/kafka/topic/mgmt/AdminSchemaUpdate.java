package se.yolean.kafka.topic.mgmt;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

public class AdminSchemaUpdate {

  private ILogger log = SLoggerFactory.getLogger(this.getClass());

  private SchemaRegistryClient client;

  @Inject
  public AdminSchemaUpdate(SchemaRegistryClient client) {
    this.client = client;
  }

  public void getCurrentSchemaVersion() throws IOException, RestClientException {
    Collection<String> allSubjects = client.getAllSubjects();
    for (String subject : allSubjects) {
      SchemaMetadata metadata = client.getLatestSchemaMetadata(subject);
      log.debug("Found schema", "subject", subject, "id", metadata.getId(), "version", metadata.getVersion());
      log.debug("" + metadata.getSchema());
    }
  }

}
