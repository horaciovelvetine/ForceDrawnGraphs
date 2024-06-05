package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiRecValue.TXT_VAL_TYPE;

public class WikiDataEdge extends Edge {
  private String propertyQID;
  private String value; // either QID/or String: (date, quantity, actual string, url... etc)
  private String datatype; // the string value for .datatype() from the snak
  private SNAK_SRC snakType; // MAIN_SNAK or QUALIFIER
  // below values are only used when the snak source is a Qualifier
  private ContextSnakRec contextSnakRec;

  public WikiDataEdge(WikiRecSnak snak, String srcVertexQID) {
    super(srcVertexQID, findSnakTargetQID(snak));
    this.snakType = SNAK_SRC.MAIN_SNAK;
    this.propertyQID = snak.property().value();
    this.value = findSnakTargetValue(snak);
    this.datatype = snak.datatype();
    this.contextSnakRec = null;
  }

  public WikiDataEdge(WikiRecSnak snak, WikiDataEdge mainEdgeContext, int groupID) {
    super(mainEdgeContext.tgtVertexQID(), findSnakTargetQID(snak));
    this.snakType = SNAK_SRC.QUALIFIER;
    this.propertyQID = snak.property().value();
    this.value = findSnakTargetValue(snak);
    this.datatype = snak.datatype();
    this.contextSnakRec = new ContextSnakRec(mainEdgeContext, groupID);
  }

  public enum SNAK_SRC {
    MAIN_SNAK, QUALIFIER
  }

  public String propertyQID() {
    return propertyQID;
  }

  public String value() {
    return value;
  }

  public SNAK_SRC snakType() {
    return snakType;
  }

  public String datatype() {
    return datatype;
  }

  public ContextSnakRec contextSnakRec() {
    return contextSnakRec;
  }

  @Override
  public String toString() {
    return "WikiDataEdge [propertyQID=" + propertyQID + ", value=" + value + ", snakType="
        + snakType + ", datatype=" + datatype + "]";
  }

  /**
   * Determines if this is a TIME value, which will not yet have a QID (target) for the Vertex. 
   * If this is the case, the QID returned will be null.
   */
  private static String findSnakTargetQID(WikiRecSnak snak) {
    if (snak.value().type() != TXT_VAL_TYPE.ENTITY)
      return null;
    return snak.value().value();
  }

  /**
   * Determines if this is a TIME value, will return that value to set on qualifier Edges. 
   */
  private static String findSnakTargetValue(WikiRecSnak snak) {
    if (snak.value().type() == TXT_VAL_TYPE.TIME)
      return snak.value().value();
    return null;
  }

  /**
   * A private record to hold the additional details of a Qualifier Snak.
   */
  private record ContextSnakRec(String srcVertexQID, String tgtVertexQID, String propertyQID,
      String value, String type, int groupID) {

    public ContextSnakRec(WikiDataEdge mainEdgeContext, int groupID) {
      this(mainEdgeContext.srcVertexQID(), mainEdgeContext.tgtVertexQID(),
          mainEdgeContext.propertyQID(), mainEdgeContext.value(), mainEdgeContext.datatype(),
          groupID);
    }
  }
}
