package se.yolean.kafka.topic.client.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import io.prometheus.client.CollectorRegistry;
import se.yolean.kafka.topic.client.cli.Client;

public class ItestProps extends AbstractModule {

  public static final CollectorRegistry PROMETHEUS_REGISTRY = new CollectorRegistry();

  //public static final ItestProps DOCKER_COMPOSE = new ItestProps("itest-dockercompose.properties");
  public static final ItestProps DOCKER_COMPOSE = new ItestProps(new File("src/itest/resources/itest-dockercompose.properties"));

  private Properties config;

  public ItestProps(String itestPropertiesFielnameInClasspathRoot) {
    Properties properties = new Properties();
    try {
      InputStream defaultProperties = Client.class.getResourceAsStream(Client.DEFAULT_PROPERTIES_FILE);
      properties.load(defaultProperties);
      InputStream itestProperties = this.getClass().getResourceAsStream(itestPropertiesFielnameInClasspathRoot);
      properties.load(itestProperties);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.config = properties;
  }

  public ItestProps(File itestPropertiesFile) {
    Properties properties = new Properties();
    try {
      FileReader defaults = new FileReader(new File("src/main/resources/" + Client.DEFAULT_PROPERTIES_FILE));
      properties.load(defaults);
      FileReader itest = new FileReader(itestPropertiesFile);
      properties.load(itest);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.config = properties;
  }

  @Override
  protected void configure() {
    System.out.print("Itest props: ");
    this.config.list(System.out);
    Names.bindProperties(super.binder(), this.config);
  }

}
