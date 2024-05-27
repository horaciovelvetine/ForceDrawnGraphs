package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

public class VertexDetails {
  private String QID;
  private String label;
  private String description;

  public VertexDetails(ItemDocument doc) {
    this.QID = doc.getEntityId().getId();
    this.label = doc.findLabel("en");
    this.description = doc.findDescription("en");
  }

  public String QID() {
    return QID;
  }

  public String label() {
    return label;
  }

  public String description() {
    return description;
  }
}
