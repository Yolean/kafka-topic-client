package se.yolean.kafka.topic.client.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Provider;

import com.nurkiewicz.asyncretry.AsyncRetryExecutor;

public class ExecutorRetryProviderForInit implements Provider<AsyncRetryExecutor> {

  private ScheduledExecutorService concurrency;

  @Inject
  public ExecutorRetryProviderForInit(ScheduledExecutorService concurrency) {
    this.concurrency = concurrency;
  }

  @Override
  public AsyncRetryExecutor get() {
    AsyncRetryExecutor executor = new AsyncRetryExecutor(concurrency)
        //.retryOn(Throwable.class)
        .withExponentialBackoff(500, 2)     //500ms times 2 after each retry
        .withMaxDelay(10_000)               //10 seconds
        .withUniformJitter()                //add between +/- 100 ms randomly
        .withMaxRetries(20);
    return executor;
  }

}
