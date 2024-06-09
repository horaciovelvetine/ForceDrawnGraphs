package edu.ForceDrawnGraphs.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

import edu.ForceDrawnGraphs.models.Vertex;

public class WikiDataVertex extends Vertex {
  private String description;

  public WikiDataVertex(ItemDocument doc) {
    super(doc.getEntityId().getId(), doc.findLabel("en"));
    this.description = doc.findDescription("en");
  }

  public String QID() {
    return ID();
  }

  public void setQID(String QID) {
    this.setID(QID);
  }

  public String label() {
    return label();
  }

  public void setLabel(String label) {
    this.setLabel(label);
  }

  public String description() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
