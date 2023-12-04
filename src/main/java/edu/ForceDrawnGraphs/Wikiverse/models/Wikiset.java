package edu.ForceDrawnGraphs.Wikiverse.models;

import java.util.Date;

public class Wikiset {
  private int id;
  private Date createdOn;
  private Date updatedOn;

  public Wikiset() {
    this.id = 0;
    this.createdOn = new Date();
    this.updatedOn = new Date();
  }
}
