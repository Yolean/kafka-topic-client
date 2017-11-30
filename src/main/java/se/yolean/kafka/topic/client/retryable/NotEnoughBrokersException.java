package se.yolean.kafka.topic.client.retryable;

public class NotEnoughBrokersException extends Exception {

  private static final long serialVersionUID = 1L;

  public NotEnoughBrokersException(int expected, int actual) {
    super("Got " + actual + " brokers but at least " + expected + " is required");
  }

}
