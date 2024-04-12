package edu.ForceDrawnGraphs.models;

/**
 * Base class for a Vertex object (also known as a Node).
 */
public class Vertex {
  private int id;
  private float x;
  private float y;
  private float z;
  private String srcItemId;
  private String srcPageId;
  private String label;
  private String description;
  private String views;

  public Vertex() {
    //Default no vars constructor
  }

  /**
   * Constructs a new Vertex object with the given item.
   *
   * @param item The item associated with the vertex.
   * @implNote Used on initialization pre DB commit.
   */
  public Vertex(Item item) {
    this.srcItemId = item.getItemID();
    this.label = item.getEnLabel();
    this.description = item.getEnDescription();
  }

  /**
   * Constructs a new Vertex object with the given page.
   *
   * @param page The page associated with the vertex.
   * @implNote Used on initialization pre DB commit.
   */
  public Vertex(Page page) {
    this.srcPageId = page.getPageID();
    this.label = page.getTitle();
    this.views = page.getViews();
  }

  /**
   * Constructs a new Vertex object with the given item and page.
   *
   * @param item The item associated with the vertex.
   * @param page The page associated with the vertex.
   * 
   * @implNote Used on initialization pre DB commit.
   */
  public Vertex(Item item, Page page) {
    this.srcItemId = item.getItemID();
    this.label = item.getEnLabel();
    this.description = item.getEnDescription();
    this.srcPageId = page.getPageID();
    this.views = page.getViews();
  }

  // Setters
  public void setId(int id) {
    this.id = id;
  }

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void setZ(float z) {
    this.z = z;
  }

  public void setSrcItemID(String srcItemId) {
    this.srcItemId = srcItemId;
  }

  public void setSrcPageID(String srcPageId) {
    this.srcPageId = srcPageId;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setViews(String views) {
    this.views = views;
  }

  // Getters
  public int getId() {
    return id;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getZ() {
    return z;
  }

  public String getSrcItemID() {
    return srcItemId;
  }

  public String getSrcPageID() {
    return srcPageId;
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getViews() {
    return views;
  }
  //! ===========================================================================
  //! ENDS GETTERS AND SETTER INFRASTRUCTURE
  //! ===========================================================================

  /**
   * Creates a new Vertex object from the given item and page.
   *
   * @param item The item associated with the vertex.
   * @param page The page associated with the vertex.
   * @return A new Vertex object.
   */

  public static Vertex createNewVertexFromRecords(Item item, Page page) {
    if (page != null) {
      return new Vertex(item, page);
    }
    return new Vertex(item);
  }

  /**
   * Creates a new Vertex object from the given item.
   *
   * @param item The item associated with the vertex.
   * @return A new Vertex object.
   */
  public static Vertex createNewVertexFromRecords(Page page) {
    return new Vertex(page);
  }

  /**
   * Creates a new Vertex object from the given page.
   *
   * @param page The page associated with the vertex.
   * @return A new Vertex object.
   */

  public static Vertex createNewVertexFromRecords(Item item) {
    return new Vertex(item);
  }
}
