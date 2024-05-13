package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

public class Vertex {
  private String label;
  private String description;
  private String QID;

  public Vertex(ItemDocument document) {
    this.label = document.findLabel("en");
    this.description = document.findDescription("en");
    this.QID = document.getEntityId().getId();
  }
}
