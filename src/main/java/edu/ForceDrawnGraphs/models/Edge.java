package edu.ForceDrawnGraphs.models;

import java.util.Objects;

public class Edge {
  private String srcVertexID;
  private String tgtVertexID;
  private double weight;

  public Edge(String srcVertexQID, String tgtVertexQID) {
    // this.details = details;
    this.srcVertexID = srcVertexQID;
    this.tgtVertexID = tgtVertexQID;
  }

  public String srcVertexID() {
    return srcVertexID;
  }

  public String tgtVertexID() {
    return tgtVertexID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Edge edge = (Edge) o;
    return Double.compare(edge.weight, weight) == 0 && Objects.equals(srcVertexID, edge.srcVertexID)
        && Objects.equals(tgtVertexID, edge.tgtVertexID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(srcVertexID, tgtVertexID, weight);
  }
}
