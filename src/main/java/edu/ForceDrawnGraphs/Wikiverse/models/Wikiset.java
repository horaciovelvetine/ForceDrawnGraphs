package edu.ForceDrawnGraphs.Wikiverse.models;

import java.util.Date;

import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

public class Wikiset implements Loggable {
  private static final String[] RECORD_FILES = { "page.csv", "item.csv", "property.csv", "item_aliases.csv",
      "property_aliases.csv", "link_annotated_text.jsonl", "statements.csv" };

  private Date createdOn;
  private Date updatedOn;
  private RecordTotals recordTotals;
  private RecordLineImportProgress recordLineImportProgress;

  public Wikiset() {
    this.createdOn = new Date();
    this.updatedOn = new Date();
    this.recordTotals = new RecordTotals();
    this.recordLineImportProgress = new RecordLineImportProgress();
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public Date getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(Date updatedOn) {
    this.updatedOn = updatedOn;
  }

  public RecordTotals getRecordTotals() {
    return recordTotals;
  }

  public void setRecordTotals(RecordTotals recordTotals) {
    this.recordTotals = recordTotals;
  }

  public RecordLineImportProgress getRecordLineImportProgress() {
    return recordLineImportProgress;
  }

  public void setRecordLineImportProgress(RecordLineImportProgress recordLineImportProgress) {
    this.recordLineImportProgress = recordLineImportProgress;
  }

  @Override
  public String toString() {
    return "Wikiset [createdOn=" + createdOn + ", updatedOn=" + updatedOn + "\n" + recordLineImportProgress.toString()
        + "\n" + recordTotals.toString() + "]";
  }

  public void countRecordTotals() {
    this.recordTotals = new RecordTotals();
    for (String recordFile : RECORD_FILES) {
      this.recordTotals.countRecords(recordFile);
    }

  }
}
