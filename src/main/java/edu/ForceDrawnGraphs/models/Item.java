package edu.ForceDrawnGraphs.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Item extends BaseDatasetRecord {
  public String RESOURCE_FILE_NAME = "item.csv";
  private String itemID;
  private String enLabel;
  private String enDescription;

  private Item(int id, String itemID, String enLabel, String enDescription) {
    super(id);
    this.itemID = itemID;
    this.enLabel = enLabel;
    this.enDescription = enDescription;
  }

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public String getEnLabel() {
    return enLabel;
  }

  public void setEnLabel(String enLabel) {
    this.enLabel = enLabel;
  }

  public String getEnDescription() {
    return enDescription;
  }

  public void setEnDescription(String enDescription) {
    this.enDescription = enDescription;
  }

  //! ===========================================================================
  //! ENDS GETTERS AND SETTER INFRASTRUCTURE
  //! ===========================================================================

  public static Item mapSqlRowSetToItem(SqlRowSet results) {
    return new Item(results.getInt("id"), results.getString("item_id"), results.getString("en_label"),
        results.getString("en_description"));
  }
}
