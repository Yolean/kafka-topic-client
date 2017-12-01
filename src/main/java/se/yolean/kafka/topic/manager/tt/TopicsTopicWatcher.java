package se.yolean.kafka.topic.manager.tt;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

public class TopicsTopicWatcher implements Runnable {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  @Inject
  @Named("topic.declarations.consumer.polls.max")
  private int pollsMax;

  @Override
  public void run() {
    for (int i = 0; pollsMax == -1 || i < pollsMax; i++) {
      log.debug("Here we'll be repeating the topic management loop");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException("ouch", e);
      }
    }
  }

}
