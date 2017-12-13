package se.yolean.kafka.topic.client.retryable;

import java.util.concurrent.Callable;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

public class SchemaRegistrySetup implements Callable<SchemaRegistrySetup.AdminSchemaStatus> {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  @Override
  public AdminSchemaStatus call() throws Exception {
    log.warn("TODO idempotent conf of admin schema");
    return new AdminSchemaStatus();
  }

  public static class AdminSchemaStatus {
  }

}
