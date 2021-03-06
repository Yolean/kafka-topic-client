import kafka.admin.AdminOperationException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.admin.RackAwareMode.Safe$;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class Client {

  final static String topicName = System.getenv("TOPIC_NAME");
  final static boolean resetTopic = Boolean.parseBoolean(System.getenv("RESET_TOPIC"));
  final static int partitions = Integer.parseInt(System.getenv("NUM_PARTITIONS"));
  final static int replication = Integer.parseInt(System.getenv("NUM_REPLICAS"));
  final static RackAwareMode rackAwareMode = Safe$.MODULE$;

  final static int nRetries = Integer.parseInt(System.getenv("NUM_CREATE_RETRIES"));

  final static String zookeeperConnect = System.getenv("ZOOKEEPER_CONNECT");

  public static void main(String[] args) throws Exception {
    if (topicName.length() < 1) throw new Exception("Missing environment variable 'TOPIC_NAME'!");
    if (zookeeperConnect.length() < 1) throw new Exception("Missing environment variable 'ZOOKEEKER_CONNECT'");

    System.out.println("Connecting to zookeeper using address '" + zookeeperConnect + "'");

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
      AdminUtils.createTopic(zkUtils, topicName, partitions, replication, topicConfig, rackAwareMode);
    } catch (Exception e) {
      System.err.println("Topic create failed due to " + e.toString());
      if (nRetriesLeft <= 0) {
        throw new RuntimeException("Failed to create topic \"" + topicName + "\". Is Kafka and Zookeeper running?");
      } else {
        System.out.println("Failed to create topic, trying again in 5 seconds...");
        TimeUnit.SECONDS.sleep(5);
        tryCreate(zkUtils, topicName, nRetriesLeft - 1);
      }
    }

    System.out.println("Successfully created topic '" + topicName + "'");
  }

}