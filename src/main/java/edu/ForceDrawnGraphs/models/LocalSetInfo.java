package edu.ForceDrawnGraphs.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import edu.ForceDrawnGraphs.util.FindTotalRecordsInFile;

public class LocalSetInfo implements FindTotalRecordsInFile {
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
    this.itemsImported = 1;
    this.pagesImported = 1;
    this.hyperlinksImported = 1;
    this.propertiesImported = 1;
    this.statementsImported = 1;
    this.totalItems = 1;
    this.totalPages = 1;
    this.totalHyperlinks = 1;
    this.totalProperties = 1;
    this.totalStatements = 1;
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

  public void incrementImported(String target) {
    switch (target) {
      case "item.csv":
        this.itemsImported++;
        break;
      case "page.csv":
        this.pagesImported++;
        break;
      case "link_annotated_text.jsonl":
        this.hyperlinksImported++;
        break;
      case "property.csv":
        this.propertiesImported++;
        break;
      case "statements.csv":
        this.statementsImported++;
        break;
      default:
        break;
    }
  }

  public int getImportProgress(String target) {
    switch (target) {
      case "item.csv":
        return this.itemsImported;
      case "page.csv":
        return this.pagesImported;
      case "link_annotated_text.jsonl":
        return this.hyperlinksImported;
      case "property.csv":
        return this.propertiesImported;
      case "statements.csv":
        return this.statementsImported;
      default:
        return 1;
    }
  }

  public String getSQLUpdateQuery() {
    return "UPDATE local_set_info SET " +
        "items_imported = " + this.itemsImported + ", " +
        "total_items = " + this.totalItems + ", " +
        "pages_imported = " + this.pagesImported + ", " +
        "total_pages = " + this.totalPages + ", " +
        "hyperlinks_imported = " + this.hyperlinksImported + ", " +
        "total_hyperlinks = " + this.totalHyperlinks + ", " +
        "properties_imported = " + this.propertiesImported + ", " +
        "total_properties = " + this.totalProperties + ", " +
        "statements_imported = " + this.statementsImported + ", " +
        "total_statements = " + this.totalStatements + " " +
        "WHERE id = 1;";
  }

  public void findRecordTotals() {
    if (this.totalItems == 1) {
      this.totalItems = findTotalRecordsInFile("data/item.csv");
    }
    if (this.totalPages == 1) {
      this.totalPages = findTotalRecordsInFile("data/page.csv");
    }
    if (this.totalHyperlinks == 1) {
      this.totalHyperlinks = findTotalRecordsInFile("data/link_annotated_text.jsonl");
    }
    if (this.totalProperties == 1) {
      this.totalProperties = findTotalRecordsInFile("data/property.csv");
    }
    if (this.totalStatements == 1) {
      this.totalStatements = findTotalRecordsInFile("data/statements.csv");
    }
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
