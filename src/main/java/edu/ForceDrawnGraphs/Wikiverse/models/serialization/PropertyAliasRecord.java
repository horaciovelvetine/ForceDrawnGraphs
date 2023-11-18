package edu.ForceDrawnGraphs.Wikiverse.models.serialization;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "enAlias"})
public class PropertyAliasRecord {
    public final int id;
  public final String enAlias;

  public PropertyAliasRecord(int id, String enAlias) {
    this.id = id;
    this.enAlias = enAlias;
  }
}

