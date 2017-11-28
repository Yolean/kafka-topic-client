package se.yolean.kafka.topic.client.service;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class AdminClientPropsProvider implements Provider<Properties> {

	private String bootstrap;
	private String acks;

	@Inject
	public AdminClientPropsProvider(@Named("config:bootstrap") String bootstrap,
			@Named("config:acks") String acks) {
		this.bootstrap = bootstrap;
		this.acks = acks;
	}

	@Override
	public Properties get() {
		// https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
		Properties props = new Properties();
		props.put("bootstrap.servers", bootstrap);
		props.put("acks", acks);
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}

}