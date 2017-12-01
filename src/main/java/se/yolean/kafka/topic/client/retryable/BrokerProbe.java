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

  static final Counter timeouts = Counter.build()
      .name("kafkatopics_timeouts").help("AdminClient.describeCluster timeouts")
      .labelNames("broker_probe").register();

  @Inject
  private AdminClient adminClient;

  @Inject
  @Named("brokers.describe.timeout.ms")
  private int describeTimeoutMs;

  @Inject
  @Named("brokers.describe.get.timeout.ms")
  private int nodesTimeoutMs;

  @Inject
  @Named("brokers.describe.available.min")
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
      log.error("Interrupted when waiting for nodes status", e);
      throw e;
    } catch (ExecutionException e) {
      if (e.getCause() instanceof org.apache.kafka.common.errors.TimeoutException) {
        log.warn("Timeout waiting for describe nodes", "ms", describeTimeoutMs, "exception", e.getClass(),
            "cause", e.getCause().getClass(), "causeMsg", e.getCause().getMessage());
        timeouts.labels("broker_probe").inc();
        throw (org.apache.kafka.common.errors.TimeoutException) e.getCause();
      } else {
        log.error("Execution error for nodes status", e);
        throw e;
      }
    } catch (TimeoutException e) {
      log.warn("Timeout waiting for nodes response", "ms", nodesTimeoutMs);
      timeouts.labels("broker_probe").inc();
      throw e;
    } finally {
      adminClient.close(); // depends on provider impl
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
