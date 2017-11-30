package se.yolean.kafka.topic.client.config;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * Consume the admin topic.
 */
public class AdminConsumerPropsProvider implements Provider<Properties> {

  /**
   * Same ID in all replicas, means they act as a consumer group.
   *
   * Value = {@value}
   */
  public static final String CONSUMER_GROUP_ID = "kafka-topic-client";

	private String bootstrap;

	@Inject
	public AdminConsumerPropsProvider(@Named("config:bootstrap") String bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public Properties get() {
     Properties props = new Properties();
     props.put("bootstrap.servers", bootstrap);
     props.put("group.id", CONSUMER_GROUP_ID);
     props.put("enable.auto.commit", "false");
     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

}
