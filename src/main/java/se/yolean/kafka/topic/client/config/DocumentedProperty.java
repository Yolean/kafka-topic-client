package se.yolean.kafka.topic.client.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentedProperty {

  private static final Map<String, DocumentedProperty> all = new LinkedHashMap<>(1);

  static {
    new DocumentedProperty("bootstrap.servers", Type.Str, true)
      .setDescription("What any Kafka client nees");
  }

  public static final boolean has(String key) {
    return all.containsKey(key);
  }

  public static final DocumentedProperty get(String key) {
    return all.get(key);
  }

  /**
   * Note that any property used as @Inject will be required regardless.
   */
  public static final List<String> getRequired() {
    return all.entrySet().stream()
      .filter(p -> p.getValue().isRequired())
      .map(p -> p.getKey())
      .collect(Collectors.toList());
  }

  public enum Type {
    Str,
    Int,
    Bool
  }

  private String key;
  private Type type;
  private boolean isRequired;
  private String description = null;

  private DocumentedProperty(String key, Type type, boolean isRequired) {
    this.key = key;
    this.type = type;
    this.isRequired = isRequired;
    all.put(key, this);
  }

  private DocumentedProperty setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getKey() {
    return key;
  }

  public Type getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public boolean isRequired() {
    return isRequired;
  }

}
