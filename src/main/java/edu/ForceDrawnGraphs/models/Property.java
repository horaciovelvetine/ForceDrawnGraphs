package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * Class which is functionally connected to many edges in the graphset, containing details about the nature of an Edge.
 */
public record Property(String ID, String label, String description) {

  public Property {
    if (ID == null) {
      throw new IllegalArgumentException("ID cannot be null");
    }
  }

  public Property(PropertyDocument propertyDocument) {
    this(propertyDocument.getEntityId().getId(), propertyDocument.findLabel("en"),
        propertyDocument.findDescription("en"));
  }

  @Override
  public String toString() {
    return "Property{" + "ID='" + ID + '\'' + ", label='" + label + '\'' + ", description='"
        + description + '\'' + '}';
  }
}
