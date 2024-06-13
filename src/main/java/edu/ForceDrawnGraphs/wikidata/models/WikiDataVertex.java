package edu.ForceDrawnGraphs.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import edu.ForceDrawnGraphs.models.Vertex;

public class WikiDataVertex extends Vertex {
  private String description;
  private String matchingPropertyQID;

  public WikiDataVertex(ItemDocument doc) {
    super(doc.getEntityId().getId(), doc.findLabel("en"));
    this.description = doc.findDescription("en");
  }

  public WikiDataVertex(WbSearchEntitiesResult result) {
    super(result.getEntityId(), result.getLabel());
    this.description = result.getDescription();
  }

  public String QID() {
    return ID();
  }

  public String label() {
    return label();
  }

  public String description() {
    return description;
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

}
