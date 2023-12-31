package edu.ForceDrawnGraphs.Wikiverse.models.serialization;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "enLabel", "enDescription" })
public class ItemRecord {
  public final int id;
  public final String enLabel;
  public final String enDescription;

  public ItemRecord(int id, String enLabel, String enDescription) {
    this.id = id;
    this.enLabel = enLabel;
    this.enDescription = enDescription;
  }
}
