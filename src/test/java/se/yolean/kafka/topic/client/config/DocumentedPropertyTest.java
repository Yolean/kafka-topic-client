package se.yolean.kafka.topic.client.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DocumentedPropertyTest {

  @Test
  public void testBootstrapServers() {
    assertTrue(DocumentedProperty.has("bootstrap.servers"));
    assertTrue("Should be listed as required property", DocumentedProperty.getRequired()
        .stream().anyMatch(key -> "bootstrap.servers".equals(key)));
    DocumentedProperty p = DocumentedProperty.get("bootstrap.servers");
    assertNotNull(".set get should return the property", p);
    assertEquals(DocumentedProperty.Type.Str, p.getType());
    assertNotNull(p.getDescription());
  }

}
