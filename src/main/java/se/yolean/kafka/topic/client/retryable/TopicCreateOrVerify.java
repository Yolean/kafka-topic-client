package se.yolean.kafka.topic.client.retryable;

import java.util.concurrent.Callable;

public interface TopicCreateOrVerify extends Callable<TopicOperationResult> {

}
