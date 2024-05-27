package edu.ForceDrawnGraphs.models.wikidata.models;

import edu.ForceDrawnGraphs.models.Property;

public class EdgeDetails {
  private String propQID; // ent from the mw api
  private String valueTgt; // for non-ent values
  private SourceType source; // determines what kind of data this edge is based on
  private Property property; // used to persist info from the mw api
  private EdgeDetails contextEdge; // the mainSnak associate with a QUALIFIER or REFERENCE edge

  public enum SourceType {
    SNAK_MAIN,
    SNAK_QUALIFIER,
    SNAK_REFERENCE,
    HYPERLINK
  }

  public EdgeDetails(ValueDetails propDetails, SourceType source) {
    this.propQID = propDetails.QID();
    this.source = source;
    this.valueTgt = null; // not relevant for this type of edge
    this.property = null; // fetched later
  }

  public void setValueTgt(String valueTgt) {
    // set when the relevant value is a date or string (for which no Vertex or QID yet exists)
    this.valueTgt = valueTgt;
  }

  public void setContextEdge(EdgeDetails contextEdge) {
    // set for qualifier and reference edges to associate them with their mainSnak
    // and allow for understanding of what the edge means in the larger context
    this.contextEdge = contextEdge;
  }

  public String propQID() {
    return propQID;
  }

  public String valueTgt() {
    return valueTgt;
  }

  public SourceType source() {
    return source;
  }

  public Property property() {
    return property;
  }

  public EdgeDetails contextEdge() {
    return contextEdge;
  }
}
