package se.yolean.kafka.topic.manager.configure;

import com.google.inject.AbstractModule;

import se.yolean.kafka.topic.client.retryable.BrokerProbe;
import se.yolean.kafka.topic.client.retryable.RestProxySetup;
import se.yolean.kafka.topic.client.retryable.SchemaRegistrySetup;

public class TopicDeclarationsPollModule extends AbstractModule {

  public TopicDeclarationsPollModule(
      BrokerProbe.KafkaStatus initResult,
      SchemaRegistrySetup.AdminSchemaStatus adminSchemaStatus,
      RestProxySetup.EndpointsStatus restEndpointsStatus
      ) {
  }

  @Override
  protected void configure() {
    // TODO Auto-generated method stub

  }

}
