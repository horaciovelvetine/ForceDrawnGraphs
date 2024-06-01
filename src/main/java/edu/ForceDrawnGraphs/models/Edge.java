package edu.ForceDrawnGraphs.models;

import java.util.Objects;

public class Edge {
  private String srcVertexQID;
  private String tgtVertexQID;
  private double weight;

  public Edge(String srcVertexQID, String tgtVertexQID) {
    // this.details = details;
    this.srcVertexQID = srcVertexQID;
    this.tgtVertexQID = tgtVertexQID;
  }


  public String srcVertexQID() {
    return srcVertexQID;
  }

  public String tgtVertexQID() {
    return tgtVertexQID;
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
