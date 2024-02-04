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
    this.recordTotals = countRecordTotals();
    this.recordLineImportProgress = new RecordLineImportProgress();
  }

  public Wikiset(Date createdOn, Date updatedOn, RecordTotals recordTotals,
      RecordLineImportProgress recordLineImportProgress) {
    this.createdOn = createdOn;
    this.updatedOn = updatedOn;
    this.recordTotals = recordTotals;
    this.recordLineImportProgress = recordLineImportProgress;
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

  // ! ENDS GETTERS & SETTERS

  @Override
  public String toString() {
    return "WIKISET:" + "[createdOn=" + createdOn + ", updatedOn=" + updatedOn + "\n"
        + recordLineImportProgress.toString()
        + "\n" + recordTotals.toString() + "]";
  }

  public RecordTotals countRecordTotals() {
    RecordTotals totals = new RecordTotals();
    for (String recordFile : RECORD_FILES) {
      totals.countRecords(recordFile);
    }
    print("Record totals updated:" + "\n" + totals.toString());
    this.setUpdatedOn(new Date());
    return totals;
  }
}
