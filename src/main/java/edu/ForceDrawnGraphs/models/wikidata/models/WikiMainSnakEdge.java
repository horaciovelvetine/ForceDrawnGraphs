package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;

public class WikiMainSnakEdge extends Edge {
  private final String propertyQID;
  private final String value;

  public WikiMainSnakEdge(String srcVertexQID, String tgtVertexQID, String propertyQID, String value) {
    super(srcVertexQID, tgtVertexQID);
    this.propertyQID = propertyQID;
    this.value = value;
  }

  public String propertyQID() {
    return propertyQID;
  }

  public String value() {
    return value;
  }

}
