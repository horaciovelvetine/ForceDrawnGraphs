package edu.ForceDrawnGraphs.Wikiverse.serial;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "enLabel", "enDescription"})
public class PropertyRecord {
  public final int id;
  public final String enLabel;
  public final String enDescription;

  public PropertyRecord(int id, String enLabel, String enDescription) {
    this.id = id;
    this.enLabel = enLabel;
    this.enDescription = enDescription;
  }
}
