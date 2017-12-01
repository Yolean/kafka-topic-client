package se.yolean.kafka.topic.client.config;

import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;

public class ConcurrencyModule extends AbstractModule {

  @Override
  protected void configure() {
    // Using Scheduled because the retry lib depend(s|ed) on it
    bind(ScheduledExecutorService.class).toProvider(ExecutorServiceProvider.class);

    // I'm not so sure we should use this lib...
    // Our callables have quite different characteristics,
    // with kafka libs doing back-off within given timeouts.
    // And we can probably do simple retries, while managing concurrency as suggested in
    // http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
    //bind(com.nurkiewicz.asyncretry.AsyncRetryExecutor.class).toProvider(ExecutorRetryProviderForInit.class);
  }

}
