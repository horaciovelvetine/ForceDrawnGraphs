package edu.ForceDrawnGraphs.models;

import java.util.Objects;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Vertex {
  private Double x;
  private Double y;
  private String id;
  private String label;
  private String description;
  private String matchingPropertyQID;

  public Vertex() {
    // Default requirement for Jackson
  }

  public Vertex(String id, String label) {
    this.id = id;
    this.label = label;
  }

  public Vertex(ItemDocument itemDocument) {
    this(itemDocument.getEntityId().getId(), itemDocument.findLabel("en"));
    this.description = itemDocument.findDescription("en");
  }

  public Vertex(WbSearchEntitiesResult searchResult) {
    this(searchResult.getEntityId(), searchResult.getLabel());
    this.description = searchResult.getDescription();
  }

  public String id() {
    return id;
  }

  public String label() {
    return label;
  }

  public String QID() {
    return id();
  }

  public String description() {
    return description;
  }

  public void setCoords(Double x, Double y) {
    this.x = x;
    this.y = y;
  }

  public void setMatchingPropertyQID(String matchingPropertyQID) {
    this.matchingPropertyQID = matchingPropertyQID;
  }

  public String matchingPropertyQID() {
    return matchingPropertyQID;
  }

  public boolean hasMatchingPropertyQID() {
    return matchingPropertyQID != null;
  }

  @JsonIgnore
  public boolean isInfoComplete() {
    return id != null && label != null && x != null && y != null;
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
