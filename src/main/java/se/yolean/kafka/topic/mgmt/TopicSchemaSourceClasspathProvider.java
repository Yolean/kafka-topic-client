package se.yolean.kafka.topic.mgmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.inject.Provider;

@Deprecated // not sure we'll have the schema source in classpath
public class TopicSchemaSourceClasspathProvider implements Provider<String> {

  @Override
  public String get() {
    String path = "Topic.avsc";
    InputStream source = ClassLoader.getSystemResourceAsStream(path);
    if (source == null) {
      throw new RuntimeException("Failed to read the distribution's Topic schema from " + path);
    }
    return slurp(source, 1);
  }

  public static String slurp(final InputStream is, final int bufferSize) {
    final char[] buffer = new char[bufferSize];
    final StringBuilder out = new StringBuilder();
    try (Reader in = new InputStreamReader(is, Charset.forName("UTF-8"))) {
      for (;;) {
        int rsz = in.read(buffer, 0, buffer.length);
        if (rsz < 0)
          break;
        out.append(buffer, 0, rsz);
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return out.toString();
  }

}
