package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Property;

public class EdgeQualifier extends Edge {
  private Property property;
  private String value; // either QID/or String: (date, quantity, actual string, url... etc)
  private EdgeMainSnak edgeRef; // gives context to the qualifier

  public EdgeQualifier(String srcVertexQID, String tgtVertexQID) {
    super(srcVertexQID, tgtVertexQID);

  }

}
