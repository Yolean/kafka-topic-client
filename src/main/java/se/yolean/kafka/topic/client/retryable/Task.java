package se.yolean.kafka.topic.client.retryable;

import java.util.concurrent.Callable;

/**
 * Anything that might need retries or benefit from concurrency
 * would have to be wrapped anyway, so let's have this task abstraction.
 *
 * @param <T> The type returned by the actual {@link Callable}
 *
 * @deprecated Use {@link Callable} or {@link Runnable} depending on use case.
 */
public interface Task<T> extends Callable<T> {

}
