package edu.ForceDrawnGraphs.models;

/**
 * Represents details of a statement from a Wiki Item Document.
 *  This class is created only knowing the QID for its property type, the value of the property, and the source type of the snak. The property labels are looked up later by the Queue.
 */
public class WikiDocStmtDetails {
  private String propTypeQID; // can be used to look up the prop label (tbd)
  private String propLabel; // will be null until looked up
  private String propValue; // will always be passed in - but relevant for only certain types
  private SnakSrcType snakSrcType; // denotes how the snak was sourced, and how it should be treated

  public enum SnakSrcType {
    STATEMENT,
    CLAIM,
    REFERENCE,
    QUALIFIER
  }

  /**
   * Constructs a new instance of WikiDocStmtDetails.
   *
   * @param propTypeQID   the QID of the property type
   * @param propValue     the value of the property
   * @param snakSrcType   the source type of the snak
   */
  public WikiDocStmtDetails(String propTypeQID, String propValue, SnakSrcType snakSrcType) {
    this.propTypeQID = propTypeQID;
    this.propValue = propValue;
    this.snakSrcType = snakSrcType;
  }

  public String propTypeQID() {
    return propTypeQID;
  }

  public String propLabel() {
    return propLabel;
  }

  public void setPropLabel(String propLabel) {
    this.propLabel = propLabel;
  }

  public String propValue() {
    return propValue;
  }

  public SnakSrcType snakSrcType() {
    return snakSrcType;
  }
}
