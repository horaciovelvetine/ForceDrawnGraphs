package edu.ForceDrawnGraphs.models;

import java.util.Objects;

public class Edge {
  private String srcVertexQID;
  private String tgtVertexQID;
  private double weight;
  private WikiDocStmtDetails stmtDetails;

  public Edge(String srcVertexQID, String tgtVertexQID, WikiDocStmtDetails stmtDetails) {
    this.srcVertexQID = srcVertexQID;
    this.tgtVertexQID = tgtVertexQID;
    this.stmtDetails = stmtDetails;

  }

  public WikiDocStmtDetails details() {
    return stmtDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Edge edge = (Edge) o;
    return Double.compare(edge.weight, weight) == 0 &&
        Objects.equals(srcVertexQID, edge.srcVertexQID) &&
        Objects.equals(tgtVertexQID, edge.tgtVertexQID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(srcVertexQID, tgtVertexQID, weight);
  }
}
