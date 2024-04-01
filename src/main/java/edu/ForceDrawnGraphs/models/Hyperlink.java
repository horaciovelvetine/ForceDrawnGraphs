package edu.ForceDrawnGraphs.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class Hyperlink {
  private int id;
  private int from_page_id;
  private int to_page_id;
  private int count;
  private int lineRef;

  /**
   * Constructs a hyperlink with from and to page IDs, a default count of 1, and the line reference. 
   * 
   * This constructor should only be used when creating a new hyperlink before committing it to the database.
   *
   * @param from_page_id the ID of the source page
   * @param to_page_id   the ID of the target page
   * @param lineRef      the line reference of the hyperlink
   */
  public Hyperlink(int from_page_id, int to_page_id, int lineRef) {
    this.from_page_id = from_page_id;
    this.to_page_id = to_page_id;
    this.count = 1;
    this.lineRef = lineRef;
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
   * @param lineRef       the line reference of the hyperlink
   */
  public Hyperlink(int id, int from_page_id, int to_page_id, int count, int lineRef) {
    this.id = id;
    this.from_page_id = from_page_id;
    this.to_page_id = to_page_id;
    this.count = count;
    this.lineRef = lineRef;
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
  public int getFrom_page_id() {
    return from_page_id;
  }

  /**
   * Sets the ID of the source page.
   *
   * @param from_page_id the ID of the source page
   */
  public void setFrom_page_id(int from_page_id) {
    this.from_page_id = from_page_id;
  }

  /**
   * Returns the ID of the target page.
   *
   * @return the ID of the target page
   */
  public int getTo_page_id() {
    return to_page_id;
  }

  /**
   * Sets the ID of the target page.
   *
   * @param to_page_id the ID of the target page
   */
  public void setTo_page_id(int to_page_id) {
    this.to_page_id = to_page_id;
  }

  /**
   * Returns the count of the hyperlink.
   *
   * @return the count of the hyperlink
   */
  public int getCount() {
    return count;
  }

  /**
   * Sets the count of the hyperlink.
   *
   * @param count the count of the hyperlink
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * Returns the line reference of the hyperlink.
   *
   * @return the line reference of the hyperlink
   */
  public int getLineRef() {
    return lineRef;
  }

  /**
   * Sets the line reference of the hyperlink.
   *
   * @param lineRef the line reference of the hyperlink
   */
  public void setLineRef(int lineRef) {
    this.lineRef = lineRef;
  }

  /**
   * Increments the count of the hyperlink by 1.
   */
  public void incrementCount() {
    this.count++;
  }

  /**
   * Maps the data from a SQL row set to a Hyperlink object.
   *
   * @param row the SQL row set containing the hyperlink data
   * @return the Hyperlink object mapped from the SQL row set
   */
  public static Hyperlink mapSQLRowSetToHyperlink(SqlRowSet row) {
    return new Hyperlink(row.getInt("id"), row.getInt("from_page_id"), row.getInt("to_page_id"),
        row.getInt("count"), row.getInt("line_ref"));
  }
}
