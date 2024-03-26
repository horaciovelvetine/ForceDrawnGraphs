package edu.ForceDrawnGraphs.models;

public class BaseDatasetRecord {
  private int id;
  private int lineRef;

  public BaseDatasetRecord(int id, int lineRef) {
    this.id = id;
    this.lineRef = lineRef;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getLineRef() {
    return lineRef;
  }

  public void setLineRef(int lineRef) {
    this.lineRef = lineRef;
  }
}