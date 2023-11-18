package edu.ForceDrawnGraphs.Wikiverse.models;

public class RecordTotalsInfo {
  public final int itemAliases;
  public final int items;
  public final int linksAndSectionTexts;
  public final int pages;
  public final int propertyAliases;
  public final int properties;
  public final int statements;

  public RecordTotalsInfo(int itemAliases, int items, int linkAnnotatedText, int pages, int propertyAliases,
      int properties, int statements) {
    this.itemAliases = itemAliases;
    this.items = items;
    this.linksAndSectionTexts = linkAnnotatedText;
    this.pages = pages;
    this.propertyAliases = propertyAliases;
    this.properties = properties;
    this.statements = statements;
  }

  @Override
  public String toString() {
    return "Total Records Counted:"
        + "[itemAliases=" + itemAliases
        + ", items=" + items
        + ", linksAndSectionTexts=" + linksAndSectionTexts
        + ", pages=" + pages
        + ", propertyAliases=" + propertyAliases
        + ", properties=" + properties
        + ", statements=" + statements + "]";
  }
}
