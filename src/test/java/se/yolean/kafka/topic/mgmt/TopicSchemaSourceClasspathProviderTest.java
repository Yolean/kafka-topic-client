package se.yolean.kafka.topic.mgmt;

import static org.junit.Assert.*;

import javax.inject.Provider;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TopicSchemaSourceClasspathProviderTest {

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
  @Ignore // TODO I'm undecided on how to use the source schema in dev and production
  public void testClasspath() {
    Provider<String> provider = new TopicSchemaSourceClasspathProvider();
    String schemaSourceForBuild = provider.get();
    assertNotNull(schemaSourceForBuild);
  }

  @Test
  public void testFromGenerated() {
    Provider<String> provider = new TopicSchemaSourceProvider();
    String schemaSourceForBuild = provider.get();
    assertNotNull(schemaSourceForBuild);
    assertNotEquals(0, schemaSourceForBuild.length());
    System.out.println(schemaSourceForBuild);
  }

}
