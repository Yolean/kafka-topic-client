package se.yolean.kafka.topic.client.config;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ManagerConfigModuleTest {

  @Test
  public void test() {
    Properties props = new Properties();
    props.setProperty("bootstrap.servers", "PLAINTEXT://my-test-value:9092");
    Injector injector = Guice.createInjector(new ManagerConfigModule(props));
    TestService1 t1 = injector.getInstance(TestService1.class);
    assertEquals("PLAINTEXT://my-test-value:9092", t1.boostrapServers);
  }

  static class TestService1 {

    @Inject
    @Named("bootstrap.servers")
    private String boostrapServers;

  }

}
