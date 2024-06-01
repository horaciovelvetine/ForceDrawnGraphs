package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

public record WikiRecItem(String QID, String label, String description) {
  public WikiRecItem(ItemDocument doc) {
    this(doc.getEntityId().getId(), doc.findLabel("en"), doc.findDescription("en"));
  }
}
