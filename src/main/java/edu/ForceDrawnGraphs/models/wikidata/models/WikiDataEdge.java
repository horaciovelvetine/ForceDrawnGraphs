package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;

public class WikiDataEdge extends Edge {
  private String propertyQID; // QID of the Property
  private String value; // either QID/or String: (date, quantity, actual string, url... etc)
  private SNAK_SRC snakType; // the WikiEnt from which the edge was constructed
  private String datatype; // the string value for .datatype() from the original snak

  // below values are only used when snakType is QUALIFIER
  private String contextPropertyQID; // QID of the a property to give context to this edge
  private String contextValue;

  /**
   * Constructor for a QUALIFIER sourced edge, with a property and value
   * 
   * @param srcVertexQID
   * @param tgtVertexQID
   * @param propertyQID
   * @param value
   * @param contextPropertyQID
   * @param contextValue
   * @param snakType
   * @param datatype
   * 
   */
  public WikiDataEdge(String srcVertexQID, String tgtVertexQID, String propertyQID, String value,
      String contextPropertyQID, String contextValue, SNAK_SRC snakType, String datatype) {
    super(srcVertexQID, tgtVertexQID);
    this.propertyQID = propertyQID;
    this.value = value;
    this.contextPropertyQID = contextPropertyQID;
    this.contextValue = contextValue;
    this.snakType = snakType;
    this.datatype = datatype;
  }

  public enum SNAK_SRC {
    MAIN_SNAK,
    QUALIFIER
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

  public String contextPropertyQID() {
    return contextPropertyQID;
  }

  public String contextValue() {
    return contextValue;
  }

  public String datatype() {
    return datatype;
  }
}
