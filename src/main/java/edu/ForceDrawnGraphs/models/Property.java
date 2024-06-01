package edu.ForceDrawnGraphs.models;

public class Property {
  private String QID;
  private String label;
  private String description;

  public Property(String QID, String label) {
    this.QID = QID;
    this.label = label;
  }

  public String QID() {
    return QID;
  }

  public String label() {
    return label;
  }

  public String description() {
    return description;
  }
}
