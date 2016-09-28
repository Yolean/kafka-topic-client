import kafka.admin.AdminOperationException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class Client {

  final static String topicName = System.getenv("TOPIC_NAME");
  final static boolean resetTopic = Boolean.parseBoolean(System.getenv("RESET_TOPIC"));
  final static int partitions = Integer.parseInt(System.getenv("NUM_PARTITIONS"));
  final static int replication = Integer.parseInt(System.getenv("NUM_REPLICAS"));

  final static int nRetries = Integer.parseInt(System.getenv("NUM_CREATE_RETRIES"));

  public static void main(String[] args) throws Exception {
    if (topicName.length() < 1) {
      throw new Exception("Missing environment variable 'TOPIC_NAME'!");
    }

    String zookeeperConnect = "zookeeper:2181";
    final int sessionTimeoutMs = 10 * 1000;
    final int connectionTimeoutMs = 8 * 1000;
    // Note: You must initialize the ZkClient with ZKStringSerializer.  If you don't, then
    // createTopic() will only seem to work (it will return without error).  The topic will exist in
    // only ZooKeeper and will be returned when listing topics, but Kafka itself does not create the
    // topic.
    ZkClient zkClient = new ZkClient(
        zookeeperConnect,
        sessionTimeoutMs,
        connectionTimeoutMs,
        ZKStringSerializer$.MODULE$);

    // Security for Kafka was added in Kafka 0.9.0.0
    final boolean isSecureKafkaCluster = false;
    ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);

    if (resetTopic && AdminUtils.topicExists(zkUtils, topicName)) {
      System.out.println("Deleting topic " + topicName);
      AdminUtils.deleteTopic(zkUtils, topicName);

      while (AdminUtils.topicExists(zkUtils, topicName)) {
        System.out.println("Waiting for kafka to really delete topic ...");
        TimeUnit.SECONDS.sleep(1);
      }
    }

    if (!AdminUtils.topicExists(zkUtils, topicName)) {
      tryCreate(zkUtils, topicName, nRetries);
    } else {
      System.out.println("Topic \"" + topicName + "\" already exists! Nothing to do here.");
    }

    zkClient.close();
  }

  private static void tryCreate(ZkUtils zkUtils, String topicName, int nRetriesLeft) throws InterruptedException {
    System.out.println("Creating topic " + topicName);
    Properties topicConfig = new Properties(); // add per-topic configurations settings here
    try {
      AdminUtils.createTopic(zkUtils, topicName, partitions, replication, topicConfig);
    } catch (Exception e) {
      if (nRetriesLeft <= 0) {
        throw new RuntimeException("Failed to create topic \"" + topicName + "\". Is Kafka and Zookeeper running?");
      } else {
        System.out.println("Failed to create topic, trying again in 5 seconds...");
        TimeUnit.SECONDS.sleep(5);
        tryCreate(zkUtils, topicName, nRetriesLeft - 1);
      }
    }
  }

}