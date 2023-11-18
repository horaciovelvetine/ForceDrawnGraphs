package edu.ForceDrawnGraphs.Wikiverse.models.serialization;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "pageId", "itemId", "title", "views" })
public class PageRecord {
  public final int pageId;
  public final int itemId;
  public final String title;
  public final int views;

  public PageRecord(int pageId, int itemId, String title, int views) {
    this.pageId = pageId;
    this.itemId = itemId;
    this.title = title;
    this.views = views;
  }

}
