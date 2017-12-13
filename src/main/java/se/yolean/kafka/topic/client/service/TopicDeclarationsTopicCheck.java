package se.yolean.kafka.topic.client.service;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicDeclarationsTopicCheck {

  private static final Logger log = LoggerFactory.getLogger(TopicDeclarationsTopicCheck.class);

  @Inject
  @Named("admin")
  private Properties props;

  @Inject
  @Named("config:adminInitTimeoutMs")
  private int initTimeout;

  @Inject
  @Named("config:adminTopic")
  private String topic;

  @Inject
  @Named("config:adminTopicDesiredReplicationFactor")
  private int desiredReplicationFactor;

  void createOrVerifyAdminTopic() throws StoreInitializationException {

    try (AdminClient admin = AdminClient.create(props)) {
      //
      Set<String> allTopics = admin.listTopics().names().get(initTimeout, TimeUnit.MILLISECONDS);
      if (allTopics.contains(topic)) {
        verifySchemaTopic(admin);
      } else {
        createSchemaTopic(admin);
      }
    } catch (TimeoutException e) {
      throw new StoreInitializationException(
          "Timed out trying to create or validate topic declarations topic configuration",
          e
      );
    } catch (InterruptedException | ExecutionException e) {
      throw new StoreInitializationException(
          "Failed trying to create or validate topic declarations topic configuration",
          e
      );
    }
  }

  private void createSchemaTopic(AdminClient admin) throws StoreInitializationException,
                                                           InterruptedException,
                                                           ExecutionException,
                                                           TimeoutException {
    log.info("Creating schemas topic {}", topic);

    int numLiveBrokers = admin.describeCluster().nodes()
        .get(initTimeout, TimeUnit.MILLISECONDS).size();
    if (numLiveBrokers <= 0) {
      throw new StoreInitializationException("No live Kafka brokers");
    }

    int schemaTopicReplicationFactor = Math.min(numLiveBrokers, desiredReplicationFactor);
    if (schemaTopicReplicationFactor < desiredReplicationFactor) {
      log.warn("Creating the topic declarations topic "
               + topic
               + " using a replication factor of "
               + schemaTopicReplicationFactor
               + ", which is less than the desired one of "
               + desiredReplicationFactor + ". If this is a production environment, it's "
               + "crucial to add more brokers and increase the replication factor of the topic.");
    }

    NewTopic schemaTopicRequest = new NewTopic(topic, 1, (short) schemaTopicReplicationFactor);
    schemaTopicRequest.configs(
        Collections.singletonMap(
            TopicConfig.CLEANUP_POLICY_CONFIG,
            TopicConfig.CLEANUP_POLICY_COMPACT
        )
    );
    try {
      admin.createTopics(Collections.singleton(schemaTopicRequest)).all()
          .get(initTimeout, TimeUnit.MILLISECONDS);
    } catch (ExecutionException e) {
      if (e.getCause() instanceof TopicExistsException) {
        log.warn("Topic {} exists, but was not listed. Concurrent operations?", topic);
      } else {
        throw e;
      }
    }
  }

  private void verifySchemaTopic(AdminClient admin) throws StoreInitializationException,
                                                           InterruptedException,
                                                           ExecutionException,
                                                           TimeoutException {
    log.info("Validating schemas topic {}", topic);

    Set<String> topics = Collections.singleton(topic);
    Map<String, TopicDescription> topicDescription = admin.describeTopics(topics)
        .all().get(initTimeout, TimeUnit.MILLISECONDS);

    TopicDescription description = topicDescription.get(topic);
    final int numPartitions = description.partitions().size();
    if (numPartitions != 1) {
      throw new StoreInitializationException("The topic declarations topic " + topic + " should have only 1 "
                                             + "partition but has " + numPartitions);
    }

    if (description.partitions().get(0).replicas().size() < desiredReplicationFactor) {
      log.warn("The replication factor of the topic declarations topic "
               + topic
               + " is less than the desired one of "
               + desiredReplicationFactor
               + ". If this is a production environment, it's crucial to add more brokers and "
               + "increase the replication factor of the topic.");
    }

    ConfigResource topicResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);

    Map<ConfigResource, Config> configs =
        admin.describeConfigs(Collections.singleton(topicResource)).all()
            .get(initTimeout, TimeUnit.MILLISECONDS);
    Config topicConfigs = configs.get(topicResource);
    String retentionPolicy = topicConfigs.get(TopicConfig.CLEANUP_POLICY_CONFIG).value();
    if (retentionPolicy == null || !TopicConfig.CLEANUP_POLICY_COMPACT.equals(retentionPolicy)) {
      log.error("The retention policy of the topic declarations topic " + topic + " is incorrect. "
                + "You must configure the topic to 'compact' cleanup policy to avoid Kafka "
                + "deleting your schemas after a week. "
                + "Refer to Kafka documentation for more details on cleanup policies");

      throw new StoreInitializationException("The retention policy of the topic declarations topic " + topic
                                             + " is incorrect. Expected cleanup.policy to be "
                                             + "'compact' but it is " + retentionPolicy);

    }
  }

}
