package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import com.fasterxml.jackson.annotation.JsonAutoDetect;



/**
 * Class which is functionally connected to many edges in the graphset, containing details about the nature of an Edge.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Property {

  private String ID = null;
  private String label = null;
  private String description = null;

  public Property() {
    //Default requirement for Jackson
  }

  public Property(String ID, String label, String description) {
    if (ID == null) {
      throw new IllegalArgumentException("ID cannot be null");
    }
    this.ID = ID;
    this.label = label;
    this.description = description;
  }

  public Property(PropertyDocument propertyDocument) {
    this(propertyDocument.getEntityId().getId(), propertyDocument.findLabel("en"),
        propertyDocument.findDescription("en"));
  }

  public String ID() {
    return ID;
  }

  public String label() {
    return label;
  }

  public String description() {
    return description;
  }

  @Override
  public String toString() {
    return "Property{" + "ID='" + ID + '\'' + ", label='" + label + '\'' + ", description='"
        + description + '\'' + '}';
  }
}
