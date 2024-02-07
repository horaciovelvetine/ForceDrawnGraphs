package edu.ForceDrawnGraphs.models;

public class BuildLocalSetProgressTracker {
  private int itemsImported;
  private int totalItems;
  private int pagesImported;
  private int totalPages;
  private int hyperlinksImported;
  private int totalHyperlinks;
  private int propertiesImported;
  private int totalProperties;
  private int statementsImported;
  private int totalStatements;

  public BuildLocalSetProgressTracker(int itemsImported, int totalItems, int pagesImported, int totalPages, int hyperlinksImported, int totalHyperlinks, int propertiesImported, int totalProperties, int statementsImported, int totalStatements) {
    this.itemsImported = itemsImported;
    this.totalItems = totalItems;
    this.pagesImported = pagesImported;
    this.totalPages = totalPages;
    this.hyperlinksImported = hyperlinksImported;
    this.totalHyperlinks = totalHyperlinks;
    this.propertiesImported = propertiesImported;
    this.totalProperties = totalProperties;
    this.statementsImported = statementsImported;
    this.totalStatements = totalStatements;
  }

  public int getItemsImported() {
    return itemsImported;
  }

  public void setItemsImported(int itemsImported) {
    this.itemsImported = itemsImported;
  }

  public int getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(int totalItems) {
    this.totalItems = totalItems;
  }

  public int getPagesImported() {
    return pagesImported;
  }

  public void setPagesImported(int pagesImported) {
    this.pagesImported = pagesImported;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getHyperlinksImported() {
    return hyperlinksImported;
  }

  public void setHyperlinksImported(int hyperlinksImported) {
    this.hyperlinksImported = hyperlinksImported;
  }

  public int getTotalHyperlinks() {
    return totalHyperlinks;
  }

  public void setTotalHyperlinks(int totalHyperlinks) {
    this.totalHyperlinks = totalHyperlinks;
  }

  public int getPropertiesImported() {
    return propertiesImported;
  }

  public void setPropertiesImported(int propertiesImported) {
    this.propertiesImported = propertiesImported;
  }

  public int getTotalProperties() {
    return totalProperties;
  }

  public void setTotalProperties(int totalProperties) {
    this.totalProperties = totalProperties;
  }

  public int getStatementsImported() {
    return statementsImported;
  }

  public void setStatementsImported(int statementsImported) {
    this.statementsImported = statementsImported;
  }

  public int getTotalStatements() {
    return totalStatements;
  }

  public void setTotalStatements(int totalStatements) {
    this.totalStatements = totalStatements;
  }
}
