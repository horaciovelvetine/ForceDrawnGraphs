package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

/**
 * Class which functions as the vertex (node) of a given Graphset.
 * Additionally contains the WikiDocItemDetails (label, desc, QID) from the Wikimedia API. 
 * 
 */
public class Vertex extends WikiDocItemDetails {

  private double x = 0.0;
  private double y = 0.0;

  public Vertex(ItemDocument document) {
    super(document);
  }

}
