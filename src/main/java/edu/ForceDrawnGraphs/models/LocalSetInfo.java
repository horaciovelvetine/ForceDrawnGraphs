package edu.ForceDrawnGraphs.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class LocalSetInfo {
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

  public LocalSetInfo() {
    // default constructor
    this.itemsImported = 0;
    this.totalItems = 0;
    this.pagesImported = 0;
    this.totalPages = 0;
    this.hyperlinksImported = 0;
    this.totalHyperlinks = 0;
    this.propertiesImported = 0;
    this.totalProperties = 0;
    this.statementsImported = 0;
    this.totalStatements = 0;

  }

  public LocalSetInfo(int itemsImported, int totalItems, int pagesImported, int totalPages, int hyperlinksImported,
      int totalHyperlinks, int propertiesImported, int totalProperties, int statementsImported, int totalStatements) {
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

  public void mapRowResultsToLocalSetInfo(SqlRowSet results) {
    this.setItemsImported(results.getInt("items_imported"));
    this.setTotalItems(results.getInt("total_items"));
    this.setPagesImported(results.getInt("pages_imported"));
    this.setTotalPages(results.getInt("total_pages"));
    this.setHyperlinksImported(results.getInt("hyperlinks_imported"));
    this.setTotalHyperlinks(results.getInt("total_hyperlinks"));
    this.setPropertiesImported(results.getInt("properties_imported"));
    this.setTotalProperties(results.getInt("total_properties"));
    this.setStatementsImported(results.getInt("statements_imported"));
    this.setTotalStatements(results.getInt("total_statements"));
  }

  @Override
  public String toString() {
    return "LOCAL PG DATA STATUS" + "\n" +
        "ITEMS = " + itemsImported + "/" + totalItems + "\n" +
        "PAGES = " + pagesImported + "/" + totalPages + "\n" +
        "HYPERLINK = " + hyperlinksImported + "/" + totalHyperlinks + "\n" +
        "PROPERTIES = " + propertiesImported + "/" + totalProperties + "\n" +
        "STATEMENTS = " + statementsImported + "/" + totalStatements + "\n";
  }
}
