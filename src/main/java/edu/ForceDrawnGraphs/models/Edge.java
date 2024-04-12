package edu.ForceDrawnGraphs.models;

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

  public Edge(Vertex srcVertex, Vertex tgtVertex, Hyperlink hyperlink) {
    this.srcVertexId = srcVertex.getId();
    this.tgtVertextId = tgtVertex.getId();
    this.srcHyperlinkID = hyperlink.getId();
    this.weight = Integer.parseInt(hyperlink.getCount());
  }

  public Edge(Vertex srcVertex, Vertex tgtVertex, Statement statement) {
    this.srcVertexId = srcVertex.getId();
    this.tgtVertextId = tgtVertex.getId();
    this.srcStatementID = statement.getId();
    // This has to be the start of building edge types from properties
    // this.weight = Property.evaluateWeight(statement.getEdgePropertyID());
  }
}
