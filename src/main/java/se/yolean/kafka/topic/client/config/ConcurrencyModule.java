package se.yolean.kafka.topic.client.config;

import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;
import com.nurkiewicz.asyncretry.RetryExecutor;

public class ConcurrencyModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ScheduledExecutorService.class).toProvider(ExecutorServiceProvider.class);
    bind(RetryExecutor.class).toProvider(ExecutorRetryProviderForInit.class);
  }

}
