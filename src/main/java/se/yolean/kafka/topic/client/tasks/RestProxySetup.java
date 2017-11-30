package se.yolean.kafka.topic.client.tasks;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

public class RestProxySetup implements Runnable {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  @Override
  public void run() {
    log.warn("TODO set up REST endpoint for topic creation");
  }

}
