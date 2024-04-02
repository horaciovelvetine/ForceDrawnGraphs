package edu.ForceDrawnGraphs.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Page extends BaseDatasetRecord {
  public String RESOURCE_FILE_NAME = "page.csv";
  private String pageID;
  private String itemID;
  private String title;
  private String views;

  public Page(int id, String pageID, String itemID, String title, String views) {
    super(id); // Explicitly invoke the super constructor
    this.pageID = pageID;
    this.itemID = itemID;
    this.title = title;
    this.views = views;
  }

  public String getPageID() {
    return pageID;
  }

  public void setPageID(String pageID) {
    this.pageID = pageID;
  }

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getViews() {
    return views;
  }

  public void setViews(String views) {
    this.views = views;
  }

  //! ===========================================================================
  //! ENDS GETTERS AND SETTER INFRASTRUCTURE
  //! ===========================================================================

  public static Page mapSQLRowSetToPage(SqlRowSet results) {
    return new Page(results.getInt("id"), results.getString("page_id"), results.getString("item_id"),
        results.getString("title"),
        results.getString("views"));
  }
}
