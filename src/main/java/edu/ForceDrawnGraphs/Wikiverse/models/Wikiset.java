package edu.ForceDrawnGraphs.Wikiverse.models;

import java.util.Date;

public class Wikiset {
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
}
