package edu.ForceDrawnGraphs.Wikiverse.models;

public class RecordLineImportProgress {
  private int itemAliases;
  private int items;
  private int linkAnnotatedText;
  private int pages;
  private int propertyAliases;
  private int properties;
  private int statements;

  public RecordLineImportProgress() {
    this.itemAliases = 0;
    this.items = 0;
    this.linkAnnotatedText = 0;
    this.pages = 0;
    this.propertyAliases = 0;
    this.properties = 0;
    this.statements = 0;
  }

  public RecordLineImportProgress(int itemAliases, int items, int linkAnnotatedText, int pages,
      int propertyAliases, int properties,
      int statements) {
    this.itemAliases = itemAliases;
    this.items = items;
    this.linkAnnotatedText = linkAnnotatedText;
    this.pages = pages;
    this.propertyAliases = propertyAliases;
    this.properties = properties;
    this.statements = statements;
  }

  public int getItemAliases() {
    return itemAliases;
  }

  public void setItemAliases(int itemAliases) {
    this.itemAliases = itemAliases;
  }

  public int getItems() {
    return items;
  }

  public void setItems(int items) {
    this.items = items;
  }

  public int getLinkAnnotatedText() {
    return linkAnnotatedText;
  }

  public void setLinkAnnotatedText(int linkAnnotatedText) {
    this.linkAnnotatedText = linkAnnotatedText;
  }

  public int getPages() {
    return pages;
  }

  public void setPages(int pages) {
    this.pages = pages;
  }

  public int getPropertyAliases() {
    return propertyAliases;
  }

  public void setPropertyAliases(int propertyAliases) {
    this.propertyAliases = propertyAliases;
  }

  public int getProperties() {
    return properties;
  }

  public void setProperties(int properties) {
    this.properties = properties;
  }

  public int getStatements() {
    return statements;
  }

  public void setStatements(int statements) {
    this.statements = statements;
  }
}
