package se.yolean.kafka.topic.client.tasks;

import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.CallResults;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

public class TaskRetryBackoffWrapper<T> implements Task<T> {

  private RetryConfig config;
  private Task<T> task;

  public TaskRetryBackoffWrapper(Task<T> actualTask) {
    this.task = actualTask;
    config = new RetryConfigBuilder()
        .failOnAnyException()
        .withExponentialBackoff()
        .withMaxNumberOfTries(10)
        .build();
  }

  @Override
  public T call() throws Exception {
    CallExecutor<T> executor = new CallExecutor<T>(config);
    CallResults<T> execute = executor.execute(task);
    return execute.getResult();
  }

}
