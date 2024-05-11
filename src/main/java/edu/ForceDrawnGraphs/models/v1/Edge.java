package edu.ForceDrawnGraphs.models.v1;

import java.util.Set;

public class Edge {
  private int id;
  private int srcVertexId; // actual serial ID
  private int tgtVertextId; // actual serial ID
  private int weight;
  private String edgeTypeId; // The corresponding property_id;
  private int srcStatementID;
  private int srcHyperlinkID;

  public Edge() {
    //Default no vars constructor
  }

  public Edge(Vertex srcVertex, Vertex tgtVertex, Set<Hyperlink> hyperlinks, Set<Statement> statements) {
    this.srcVertexId = srcVertex.getID();
    this.tgtVertextId = tgtVertex.getID();
    // this.srcHyperlinkID = hyperlink.getId();
    // this.weight = Integer.parseInt(hyperlink.getCount());
  }
}
