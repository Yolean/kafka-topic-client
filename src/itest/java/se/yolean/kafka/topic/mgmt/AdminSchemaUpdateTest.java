package se.yolean.kafka.topic.mgmt;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import se.yolean.kafka.topic.client.service.IntegrationTestConfigLocalhost;

public class AdminSchemaUpdateTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() throws Exception {
    Injector injector = Guice.createInjector(
        new IntegrationTestConfigLocalhost(),
        new AbstractModule() {
      @Override
      protected void configure() {
        bind(SchemaRegistryClient.class).toProvider(SchemaRegistryClientProvider.class);
      }
    });
    AdminSchemaUpdate update = injector.getInstance(AdminSchemaUpdate.class);
    update.getCurrentSchemaVersion();
  }

}
