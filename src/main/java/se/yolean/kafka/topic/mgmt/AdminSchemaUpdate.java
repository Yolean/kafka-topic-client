package se.yolean.kafka.topic.mgmt;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.avro.Schema;
import org.slf4j.Logger;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

public class AdminSchemaUpdate {

  private ILogger log = SLoggerFactory.getLogger(this.getClass());

  private SchemaRegistryClient client;

  // TODO configurable
  private String topicDeclarationSchemaName = "topic_declaration";

  private Schema topicSchema;

  @Inject
  public AdminSchemaUpdate(SchemaRegistryClient client, Schema topicSchema) {
    this.client = client;
    this.topicSchema = topicSchema;
  }

  public void createOrVerifyAdminSchema() {
    SchemaMetadata existing;
    try {
      existing = getCurrentSchema();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (RestClientException e) {
      throw new RuntimeException(e);
    }
    if (existing != null) {
      log.info("Stored schema is up-to-date", "id", existing.getId(), "version", existing.getVersion());
      return;
    }
    try {
      uploadCurrentSchema();
    } catch (IOException e) {
      throw new RuntimeException("Schema upload error", e);
    } catch (RestClientException e) {
      throw new RuntimeException("Schema upload error", e);
    }
  }

  public SchemaMetadata getCurrentSchema() throws IOException, RestClientException {
    Collection<String> allSubjects = client.getAllSubjects();
    for (String subject : allSubjects) {
      SchemaMetadata metadata = client.getLatestSchemaMetadata(subject);
      log.debug("Found schema", "subject", subject, "id", metadata.getId(), "version", metadata.getVersion());
      Schema latestSchema = client.getBySubjectAndId(subject, metadata.getId());
      if (topicSchema.equals(latestSchema)) {
        log.info("This is the topic schema!", "subject", subject, "id", metadata.getId(), "version", metadata.getVersion(), "fields", latestSchema.getFields().size());
        return metadata;
      } else {
        log.info("Not the topic schema", "subject", subject, "id", metadata.getId());
      }
    }
    return null;
  }

  public void uploadCurrentSchema() throws IOException, RestClientException {
    log.info("Uploading current schema to registry", "subject", topicDeclarationSchemaName, "json", topicSchema.toString());
    int register = client.register(topicDeclarationSchemaName, topicSchema);
    log.info("Uploaded schema", "id", register);
  }

}
