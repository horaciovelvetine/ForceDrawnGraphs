package edu.ForceDrawnGraphs.Wikiverse.serial;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "sourceItemId", "edgePropertyId", "targetItemId" })
public class StatementRecord {
  public final int sourceItemId;
  public final int edgePropertyId;
  public final int targetItemId;

  public StatementRecord(int sourceItemId, int edgePropertyId, int targetItemId) {
    this.sourceItemId = sourceItemId;
    this.edgePropertyId = edgePropertyId;
    this.targetItemId = targetItemId;
  }
}
