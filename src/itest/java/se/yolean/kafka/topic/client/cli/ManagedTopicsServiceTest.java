package se.yolean.kafka.topic.client.cli;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.junit.Test;

public class ManagedTopicsServiceTest {

  private Properties getProperties(String... props) {
    Properties config = new Properties();
    for (String path : props) {
      try {
        Reader source = new FileReader(path);
        config.load(source);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(path, e);
      } catch (IOException e) {
        throw new RuntimeException(path, e);
      }
    }
    return config;
  }

  @Test
  public void testManagedTopicsService() {
    Properties config = getProperties(
        "src/main/resources/default.properties",
        "src/itest/resources/itest-dockercompose.properties"
        );
    ManagedTopicsService service = new ManagedTopicsService(config);
    service.start();
  }

}
