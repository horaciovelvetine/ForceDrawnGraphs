package edu.ForceDrawnGraphs.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Edge {
  private String propertyQID;
  private String srcVertexID;
  private String tgtVertexID;
  private double weight;
  private String label;
  private String value;
  private String datatype;

  public Edge() {
    //Default requirement for Jackson
  }

  public Edge(String srcVertexQID, String tgtVertexQID) {
    this.srcVertexID = srcVertexQID;
    this.tgtVertexID = tgtVertexQID;
  }

  public String srcVertexID() {
    return srcVertexID;
  }

  public String tgtVertexID() {
    return tgtVertexID;
  }

  public void setTgtVertexID(String tgtID) {
    this.tgtVertexID = tgtID;
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
