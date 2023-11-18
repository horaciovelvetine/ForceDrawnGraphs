package edu.ForceDrawnGraphs.Wikiverse.serial;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"itemId", "enAlias"})
public class ItemAliasRecord {
  public final int itemId;
  public final String enAlias;

  public ItemAliasRecord(int itemId, String enAlias) {
    this.itemId = itemId;
    this.enAlias = enAlias;
  }
}
