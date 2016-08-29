import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class Client {

  final static String topicName = System.getenv("TOPIC_NAME");
  final static int partitions = Integer.parseInt(System.getenv("NUM_PARTITIONS"));
  final static int replication = Integer.parseInt(System.getenv("NUM_REPLICAS"));

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


    Properties topicConfig = new Properties(); // add per-topic configurations settings here
    AdminUtils.createTopic(zkUtils, topicName, partitions, replication, topicConfig);
    zkClient.close();
  }

}