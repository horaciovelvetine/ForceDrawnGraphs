package edu.ForceDrawnGraphs.Wikiverse.models;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import edu.ForceDrawnGraphs.Wikiverse.exceptions.UnableToGetRecordCountException;
import edu.ForceDrawnGraphs.Wikiverse.utils.Countable;
import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

public class RecordTotals implements Loggable, Countable {
  private int itemAliases;
  private int items;
  private int linkAnnotatedTexts;
  private int pages;
  private int propertyAliases;
  private int properties;
  private int statements;

  public RecordTotals() {
    this.itemAliases = 0;
    this.items = 0;
    this.linkAnnotatedTexts = 0;
    this.pages = 0;
    this.propertyAliases = 0;
    this.properties = 0;
    this.statements = 0;
  }

  public RecordTotals(int itemAliases, int items, int linkAnnotatedText, int pages, int propertyAliases, int properties,
      int statements) {
    this.itemAliases = itemAliases;
    this.items = items;
    this.linkAnnotatedTexts = linkAnnotatedText;
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

  public int getLinkAnnotatedTexts() {
    return linkAnnotatedTexts;
  }

  public void setLinkAnnotatedText(int linkAnnotatedTexts) {
    this.linkAnnotatedTexts = linkAnnotatedTexts;
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

  public void countRecords(String fileString) {
    long total = 0;
    try {
      total = countLines("src/main/resources/data/" + fileString);
      setTotalUsingFileString(fileString, (int) total);
      print("Found: " + total + " records in " + fileString);
    } catch (Exception e) {
      log(new UnableToGetRecordCountException(fileString + " :: " + e.getMessage()));
    }
  }

  public void setTotalUsingFileString(String fileString, int total) {
    switch (fileString) {
      case "item_aliases.csv":
        this.itemAliases = total;
        break;
      case "items.csv":
        this.items = total;
        break;
      case "link_annotated_text.jsonl":
        this.linkAnnotatedTexts = total;
        break;
      case "pages.csv":
        this.pages = total;
        break;
      case "property_aliases.csv":
        this.propertyAliases = total;
        break;
      case "properties.csv":
        this.properties = total;
        break;
      case "statements.csv":
        this.statements = total;
        break;
      default:
        break;
    }
  }

  public static RecordTotals mapRowSetToRecordTotals(SqlRowSet rowSet) {
    RecordTotals recordTotals = new RecordTotals();
    recordTotals.setItemAliases(rowSet.getInt("total_item_alias_records"));
    recordTotals.setItems(rowSet.getInt("total_item_records"));
    recordTotals.setLinkAnnotatedText(rowSet.getInt("total_link_annotated_text_records"));
    recordTotals.setPages(rowSet.getInt("total_page_records"));
    recordTotals.setPropertyAliases(rowSet.getInt("total_property_alias_records"));
    recordTotals.setProperties(rowSet.getInt("total_property_records"));
    recordTotals.setStatements(rowSet.getInt("total_statement_records"));
    return recordTotals;
  }

  @Override
  public String toString() {
    return "RecordTotals [itemAliases=" + itemAliases + ", items=" + items + ", linkAnnotatedText=" + linkAnnotatedTexts
        + ", pages=" + pages + ", propertyAliases=" + propertyAliases + ", properties=" + properties + ", statements="
        + statements + "]";
  }
}
