package edu.ForceDrawnGraphs.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import edu.ForceDrawnGraphs.wikidata.models.WikiRecSnak;
import edu.ForceDrawnGraphs.wikidata.models.WikiRecValue.TXT_VAL_TYPE;

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

  public Edge(WikiRecSnak snak, String srcVertexQID) {
    this.srcVertexID = srcVertexQID;
    this.tgtVertexID = findSnakTargetQID(snak);
    this.propertyQID = snak.property().value();
    this.value = findSnakTargetValue(snak);
    this.datatype = snak.datatype();
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

  public void setPropertyQID(String propertyQID) {
    this.propertyQID = propertyQID;
  }

  public String propertyQID() {
    return propertyQID;
  }

  public String value() {
    return value;
  }

  public String datatype() {
    return datatype;
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

  @Override
  public String toString() {
    return "[:propertyQID=" + urlPrefixer(propertyQID) + ", value=" + value + ", datatype="
        + datatype + ", tgtVertexQID=" + urlPrefixer(tgtVertexID()) + ", srcVertexQID="
        + urlPrefixer(srcVertexID()) + " :]";
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------
  private String urlPrefixer(String qid) {
    if (qid == null)
      return null;
    if (qid.startsWith("P")) {
      return "https://www.wikidata.org/wiki/Property:" + qid;
    } else {
      return "https://www.wikidata.org/wiki/" + qid;
    }
  }

  private static String findSnakTargetQID(WikiRecSnak snak) {
    if (snak.value().type() != TXT_VAL_TYPE.ENTITY)
      return null;
    return snak.value().value();
  }

  private static String findSnakTargetValue(WikiRecSnak snak) {
    if (snak.value().type() == TXT_VAL_TYPE.TIME)
      return snak.value().value();
    return null;
  }

}
