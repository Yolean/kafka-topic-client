package se.yolean.kafka.topic.client.tasks;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.Node;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import io.prometheus.client.Counter;

public class BrokerProbe implements Task<Integer>, Provider<BrokerStatus> {

  private final ILogger log = SLoggerFactory.getLogger(this.getClass());

  // Should be made configurable, but let's keep them short and work on back-off now
  private final int describeTimeoutMs = 1; // is this used, or overridden at each call?
  private final int nodesTimeoutMs = 10;

  static final Counter timeouts = Counter.build()
      .name("timeouts").labelNames("broker_probe").help("AdminClient.describeCluster timeouts").register();

  @Inject
  private AdminClient adminClient;

  @Override
  public Integer call() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public BrokerStatus get() {
    DescribeClusterOptions options = new DescribeClusterOptions();
    options.timeoutMs(describeTimeoutMs);
    DescribeClusterResult describe = adminClient.describeCluster(options);
    return new BrokerStatusNotCached(describe, nodesTimeoutMs);
  }

  class BrokerStatusNotCached implements BrokerStatus {

    private DescribeClusterResult describe;
    private int timeoutMs;

    private BrokerStatusNotCached(DescribeClusterResult describe, int timeoutMs) {
      this.describe = describe;
      this.timeoutMs = timeoutMs;
    }

    public Node getController() {
      try {
        return describe.controller().get(timeoutMs, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        log.error("Interrupted when waiting for controller status", e);
      } catch (ExecutionException e) {
        log.error("Execution error for controller status", e);
      } catch (TimeoutException e) {
        log.warn("Timeout waiting for controller response", "ms", timeoutMs, e);
        timeouts.inc();
      }
      return null;
    }

    public Collection<Node> getNodes() {
      try {
        return describe.nodes().get(timeoutMs, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        log.error("Interrupted when waiting for controller status", e);
      } catch (ExecutionException e) {
        log.error("Execution error for controller status", e);
      } catch (TimeoutException e) {
        log.warn("Timeout waiting for controller response", "ms", timeoutMs, e);
        timeouts.inc();
      }
      return Collections.emptySet();
    }

  }

}
