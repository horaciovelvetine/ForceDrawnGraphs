package edu.ForceDrawnGraphs.models;

public class Hyperlink {
  private int id;
  private int from_page_id;
  private int to_page_id;
  private int count;
  private int lineRef;

  /**
   * Constructs a hyperlink with with from & to page IDs, a default count of 1 and the line reference. 
   * 
   * This implementation should only be used when creating a new hyperlink, before committing it to the database.
   *
   * @param from_page_id the ID of the source page
   * @param to_page_id   the ID of the target page
   * @param lineRef     the line reference of the hyperlink
   */
  public Hyperlink(int from_page_id, int to_page_id, int lineRef) {
    this.from_page_id = from_page_id;
    this.to_page_id = to_page_id;
    this.count = 1;
    this.lineRef = lineRef;
  }

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
   * Sets the count of the hyperlink, count represents the number of times the relevant page is linked to.
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
}
