package se.yolean.kafka.topic.client.service;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.kafka.clients.admin.AdminClientConfig;

@Deprecated // Inject AdminClient directly
public class AdminClientPropsProvider implements Provider<Properties> {

	private String bootstrap;

	@Inject
	public AdminClientPropsProvider(@Named("config:bootstrap") String bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public Properties get() {
		Properties props = new Properties();
		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		return props;
	}

}