package se.yolean.kafka.topic.client.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.javafx.scene.control.Properties;

public class TopicDeclarationsTopicCheckTest {

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
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Properties.class).toInstance(new Properties());
        //bind(TopicDeclarationsTopicCheck.class);
      }
    });
    //TopicDeclarationsTopicCheck check = injector.getInstance(TopicDeclarationsTopicCheck.class);
    //check.createOrVerifySchemaTopic();
  }

}
