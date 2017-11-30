package se.yolean.kafka.topic.client.retryable;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.prometheus.client.Counter;

public class BrokerProbe implements Callable<BrokerProbe.KafkaStatus> {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  // Should be made configurable, but let's keep them short and work on back-off

  static final Counter timeouts = Counter.build().name("timeouts").labelNames("broker_probe")
      .help("AdminClient.describeCluster timeouts").register();

  @Inject
  private AdminClient adminClient;

  @Inject
  @Named("brokers.describe.timeout")
  private int describeTimeoutMs = 1;

  @Inject
  @Named("brokers.describe.get.timeout")
  private int nodesTimeoutMs = 10;

  @Inject
  @Named("brokers.available.min")
  private int brokersAvailableMin;

  @Override
  public KafkaStatus call() throws Exception {
    DescribeClusterOptions options = new DescribeClusterOptions();
    options.timeoutMs(describeTimeoutMs);
    DescribeClusterResult describe = adminClient.describeCluster(options);

    KafkaFuture<Collection<Node>> nodesFuture = describe.nodes();

    Collection<Node> nodes = null;
    try {
      nodes = nodesFuture.get(nodesTimeoutMs, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      log.error("Interrupted when waiting for controller status", e);
    } catch (ExecutionException e) {
      log.error("Execution error for controller status", e);
    } catch (TimeoutException e) {
      log.warn("Timeout waiting for controller response", "ms", nodesTimeoutMs, e);
      timeouts.inc();
    }

    if (nodes == null) {
      throw new Exception("No broker information available");
    }
    if (nodes.size() < brokersAvailableMin) {
      throw new NotEnoughBrokersException(brokersAvailableMin, nodes.size());
    }

    return new KafkaStatus();
  }

  public static class KafkaStatus {
  }

}
