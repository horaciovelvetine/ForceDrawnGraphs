package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

import edu.ForceDrawnGraphs.models.wikidata.records.ItemDocumentRec;

/**
 * Class which functions as the vertex (node) of a given Graphset.
 * Contains the WikiDocItemDetails (label, desc, QID) from the Wikimedia API. 
 * 
 */
public class Vertex {
  private ItemDocumentRec itemDetails;
  // private double x = 0.0;
  // private double y = 0.0;
  // private double z = 0.0;

  public Vertex(ItemDocument document) {
    this.itemDetails = new ItemDocumentRec(document);
  }

  public ItemDocumentRec details() {
    return itemDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Vertex vertex = (Vertex) o;
    return itemDetails.equals(vertex.itemDetails);
  }

  @Override
  public int hashCode() {
    return itemDetails.hashCode();
  }

}
