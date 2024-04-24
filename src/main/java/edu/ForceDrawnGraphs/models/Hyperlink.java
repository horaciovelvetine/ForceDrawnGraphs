package edu.ForceDrawnGraphs.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Hyperlink {
  private int id;
  private String fromPageID;
  private String toPageID;
  private String count;

  /**
   * Constructs a hyperlink with from and to page IDs, a default count of 1, and the line reference. 
   * 
   * This constructor should only be used when creating a new hyperlink before committing it to the database.
   *
   * @param from_page_id the ID of the source page
   * @param to_page_id   the ID of the target page
   */
  public Hyperlink(String fromPageID, String toPageID) {
    this.fromPageID = fromPageID;
    this.toPageID = toPageID;
    this.count = "1";
  }

  /**
   * Constructs a hyperlink with from and to page IDs, a count, and the line reference. 
   * 
   * This constructor should only be used when creating a new hyperlink before committing it to the database.
   *
   * @param id            the ID of the hyperlink
   * @param from_page_id  the ID of the source page
   * @param to_page_id    the ID of the target page
   * @param count         the count of the hyperlink
   */
  public Hyperlink(int id, String fromPageID, String toPageID, String count) {
    this.id = id;
    this.fromPageID = fromPageID;
    this.toPageID = toPageID;
    this.count = count;
  };

  /**
   * Returns the ID of the hyperlink.
   *
   * @return the ID of the hyperlink
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the ID of the source page.
   *
   * @return the ID of the source page
   */
  public String getFromPageID() {
    return fromPageID;
  }

  /**
   * Sets the ID of the source page.
   *
   * @param fromPageID the ID of the source page
   */
  public void setFromPageID(String fromPageID) {
    this.fromPageID = fromPageID;
  }

  /**
   * Returns the ID of the target page.
   *
   * @return the ID of the target page
   */
  public String getToPageID() {
    return toPageID;
  }

  /**
   * Sets the ID of the target page.
   *
   * @param toPageID the ID of the target page
   */
  public void setToPageID(String toPageID) {
    this.toPageID = toPageID;
  }

  /**
   * Returns the count of the hyperlink.
   *
   * @return the count of the hyperlink
   */
  public String getCount() {
    return count;
  }

  /**
   * Sets the count of the hyperlink.
   *
   * @param count the count of the hyperlink
   */
  public void setCount(String count) {
    this.count = count;
  }

  /**
   * Increments the count of the hyperlink by 1.
   */

  public void incrementCount() {
    int currentCount = Integer.parseInt(this.count);
    this.count = Integer.toString(currentCount + 1);
  }

  /**
   * Increments the count of the hyperlink by 1.
   */
  /**
   * Maps the data from a SQL row set to a Hyperlink object.
   *
   * @param row the SQL row set containing the hyperlink data
   * @return the Hyperlink object mapped from the SQL row set
   */
  public static Hyperlink mapSQLRowSetToHyperlink(SqlRowSet row) {
    return new Hyperlink(row.getInt("id"), row.getString("from_page_id"), row.getString("to_page_id"),
        Integer.toString(row.getInt("count")));
  }

  /**
   * Sets the values of the hyperlink and adds it to the batch for execution.
   *
   * @param stmt the prepared statement to set the values on
   */
  public void setAndAddHyperlinkToBatch(PreparedStatement stmt) {
    try {
      stmt.setString(1, this.fromPageID);
      stmt.setString(2, this.toPageID);
      stmt.setString(3, this.count);
      stmt.addBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
