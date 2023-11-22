package edu.ForceDrawnGraphs.Wikiverse.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Wikiset {
  private RecordTotalsInfo recordTotals;

  public Wikiset(RecordTotalsInfo recordTotals) {
    this.recordTotals = recordTotals;
  }

  public Wikiset(SqlRowSet results) {
    this.recordTotals = new RecordTotalsInfo(
        results.getInt("item_aliases"),
        results.getInt("items"),
        results.getInt("link_annotated_text"),
        results.getInt("pages"),
        results.getInt("property_aliases"),
        results.getInt("properties"),
        results.getInt("statements"));
  }

  public RecordTotalsInfo getRecordTotals() {
    return recordTotals;
  }

}
