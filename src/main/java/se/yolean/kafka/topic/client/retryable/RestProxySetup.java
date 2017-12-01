package se.yolean.kafka.topic.client.retryable;

import java.util.concurrent.Callable;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

public class RestProxySetup implements Callable<RestProxySetup.EndpointsStatus> {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  @Override
  public EndpointsStatus call() throws Exception {
    log.warn("TODO set up REST endpoint for topic creation");
    return new EndpointsStatus();
  }

  public static class EndpointsStatus {
  }



}
