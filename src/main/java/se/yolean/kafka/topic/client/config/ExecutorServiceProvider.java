package se.yolean.kafka.topic.client.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Provider;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

public class ExecutorServiceProvider implements Provider<ScheduledExecutorService> {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  private ScheduledExecutorService shared = null;

  @Override
  public ScheduledExecutorService get() {
    if (shared == null) {
      log.info("Creating new executor");
      shared = Executors.newSingleThreadScheduledExecutor();
    } else {
      log.warn("Reusing shared executor instance");
    }
    return shared;
  }

}
