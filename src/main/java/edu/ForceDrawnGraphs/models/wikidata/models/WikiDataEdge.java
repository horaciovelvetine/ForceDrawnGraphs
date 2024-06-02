package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;

public class WikiDataEdge extends Edge {
  private String propertyQID; // QID of the Property
  private String value; // either QID/or String: (date, quantity, actual string, url... etc)
  private EDGE_SRC srcType; // the WikiEnt from which the edge was constructed
  // only used when srcType is QUALIFIER
  private String contextPropertyQID; // QID of the a property to give context to this edge
  private String contextValue;

  private WikiDataEdge(String srcVertexQID, String tgtVertexQID) {
    // called by ea. of the public constructors to handle the Edge construction
    super(srcVertexQID, tgtVertexQID);
  }

  /**
   * Constructor for a QUALIFIER sourced edge, with a property and value
   * 
   * @param srcVertexQID
   * @param tgtVertexQID
   * @param propertyQID
   * @param value
   * @param contextPropertyQID
   * @param contextValue
   */
  public WikiDataEdge(String srcVertexQID, String tgtVertexQID, String propertyQID, String value,
      String contextPropertyQID, String contextValue, EDGE_SRC srcType) {
    this(srcVertexQID, tgtVertexQID);
    this.propertyQID = propertyQID;
    this.value = value;
    this.contextPropertyQID = contextPropertyQID;
    this.contextValue = contextValue;
    this.srcType = srcType;
  }

  public enum EDGE_SRC {
    MAIN_SNAK,
    QUALIFIER
  }

  public String propertyQID() {
    return propertyQID;
  }

  public String value() {
    return value;
  }

  public EDGE_SRC srcType() {
    return srcType;
  }

  public String contextPropertyQID() {
    return contextPropertyQID;
  }

  public String contextValue() {
    return contextValue;
  }

}
