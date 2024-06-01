package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

import edu.ForceDrawnGraphs.models.wikidata.models.WikiRecItem;

/**
 * Class which functions as the vertex (node) of a given Graphset.
 * Contains the WikiDocItemDetails (label, desc, QID) from the Wikimedia API. 
 * 
 */
public class Vertex {
  private WikiRecItem details;
  // private double x = 0.0;
  // private double y = 0.0;
  // private double z = 0.0;

  public Vertex(ItemDocument document) {
    this.details = new WikiRecItem(document);
  }

  public WikiRecItem details() {
    return details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Vertex vertex = (Vertex) o;
    return details.equals(vertex.details);
  }

  @Override
  public int hashCode() {
    return details.hashCode();
  }

}
