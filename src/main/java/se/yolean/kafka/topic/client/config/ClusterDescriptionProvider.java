package se.yolean.kafka.topic.client.config;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;

import se.yolean.kafka.topic.client.service.StoreInitializationException;

public class ClusterDescriptionProvider {

  private String bootstrap;

  @Inject
  public ClusterDescriptionProvider(@Named("config:bootstrap") String bootstrap) {
    this.bootstrap = bootstrap;
  }

  public void get() {
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);

    AdminClient admin = AdminClient.create(props);

    DescribeClusterResult describeCluster = admin.describeCluster();

  }

}
