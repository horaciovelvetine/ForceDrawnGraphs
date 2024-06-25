package edu.ForceDrawnGraphs.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Vertex {
  private String id;
  private String label;
  private String description;

  public Vertex() {
    // Default requirement for Jackson
  }

  public Vertex(String id, String label) {
    this.id = id;
    this.label = label;
  }

  public String id() {
    return id;
  }

  public String label() {
    return label;
  }

  @Override
  public String toString() {
    return "Vertex{" + "id=" + id + ", label=" + label + '}';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Vertex other = (Vertex) obj;
    return id.equals(other.id) && Objects.equals(label, other.label);
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + id.hashCode();
    result = 31 * result + Objects.hashCode(label);
    return result;
  }
}
