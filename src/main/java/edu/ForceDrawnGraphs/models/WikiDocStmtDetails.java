package edu.ForceDrawnGraphs.models;

public class WikiDocStmtDetails {
  private String srcQID; // QID of the source entity, the src item's QID where the stmt is found
  private String tgtQID; // QID of the target entity, the stmt's value
  private String propTypeQID; // QID of the property, the 'what' the relationship is
  private String propTypeLabel; // label of the property, the 'what' the relationship is
  private SnakSrcType srcType; // where in a statement the snak is found
  public enum SnakSrcType {
    STATEMENT, REFERENCE, QUALIFIER
  }
}
