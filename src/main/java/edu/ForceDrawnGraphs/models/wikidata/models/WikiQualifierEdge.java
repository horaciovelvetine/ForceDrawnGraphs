package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;

public class WikiQualifierEdge extends Edge {
  // TODO: edgeRef on sub-edges will be issue - potentially add two (three?) String (additional) attributes: contextPropQID, contextValue, (contextEntQID) 
  // private WikiMainSnakEdge edgeRef; // gives context to the qualifier
  private String propertyQID;
  private String value; // either QID/or String: (date, quantity, actual string, url... etc)

  public WikiQualifierEdge(String srcVertexQID, String tgtVertexQID, WikiMainSnakEdge edgeRef, String propertyQID,
      String value) {
    super(srcVertexQID, tgtVertexQID);
    // this.edgeRef = edgeRef;
    this.propertyQID = propertyQID;
    this.value = value;
  }

  public String propertyQID() {
    return propertyQID;
  }

  public String value() {
    return value;
  }
}
