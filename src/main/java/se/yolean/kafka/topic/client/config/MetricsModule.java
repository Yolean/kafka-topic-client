package se.yolean.kafka.topic.client.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.google.inject.AbstractModule;

import io.prometheus.client.exporter.HTTPServer;

public class MetricsModule extends AbstractModule implements Provider<MetricsModule.Exporter> {

  @Inject
  @Named("prometheus.exporter.port")
  private int port;

	@Override
	protected void configure() {
	  bind(Exporter.class).toProvider(this).asEagerSingleton();
	}

  @Override
  public Exporter get() {
    HTTPServer server;
    try {
      server = new HTTPServer(port);
    } catch (IOException e) {
      throw new RuntimeException("Failed to start metrics exporter on port " + port, e);
    }

    return new Exporter() {

      public int getHttpPort() {
        return port;
      }

      public void shutdown() {
        server.stop();
      }

      @Override
      public Status getStatus() {
        return Status.running;
      }

    };
  }

	public interface Exporter {

	  enum Status {
	    running
	  }

	  int getHttpPort();

	  void shutdown();

    Status getStatus();

	}

}
