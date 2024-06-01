package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Property;

public class EdgeMainSnak extends Edge {
  private Property property;

  public EdgeMainSnak(String srcVertexQID, String tgtVertexQID, Property property) {
    super(srcVertexQID, tgtVertexQID);
    this.property = property;
  }

  public Property property() {
    return property;
  }

}
