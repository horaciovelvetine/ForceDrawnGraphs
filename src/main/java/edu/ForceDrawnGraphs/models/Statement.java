package edu.ForceDrawnGraphs.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * Represents a statement in the dataset linking two (distinct) items. The srcItemID and tgtItemID are NON-Directional and only meant to provide a means of addressing either end of the statement edge.
 */
/**
 * Represents a statement in the application.
 * This class extends the BaseDatasetRecord class.
 */
public class Statement extends BaseDatasetRecord {
  private int srcItemID;
  private int tgtItemID;

  /**
   * Constructs a new Statement object with the specified ID, line reference, source item ID, and target item ID.
   * 
   * @param id The ID of the statement.
   * @param lineRef The line reference of the statement.
   * @param srcItemID The source item ID of the statement.
   * @param tgtItemID The target item ID of the statement.
   */
  public Statement(int id, int lineRef, int srcItemID, int tgtItemID) {
    super(id);
    this.srcItemID = srcItemID;
    this.tgtItemID = tgtItemID;
  }

  /**
   * Gets the source item ID of the statement.
   * 
   * @return The source item ID.
   */
  public int getSrcItemID() {
    return srcItemID;
  }

  /**
   * Sets the source item ID of the statement.
   * 
   * @param srcItemID The source item ID to set.
   */
  public void setSrcItemID(int srcItemID) {
    this.srcItemID = srcItemID;
  }

  /**
   * Gets the target item ID of the statement.
   * 
   * @return The target item ID.
   */
  public int getTgtItemID() {
    return tgtItemID;
  }

  /**
   * Sets the target item ID of the statement.
   * 
   * @param tgtItemID The target item ID to set.
   */
  public void setTgtItemID(int tgtItemID) {
    this.tgtItemID = tgtItemID;
  }

  /**
   * Maps a SQL row set to a Statement object.
   * 
   * @param results The SQL row set to map.
   * @return A new Statement object mapped from the SQL row set.
   */
  public static Statement mapSqlRowSetToStatement(SqlRowSet results) {
    return new Statement(results.getInt("id"), results.getInt("line_ref"),
        results.getInt("source_item_id"), results.getInt("target_item_id"));
  }
}
