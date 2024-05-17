package edu.ForceDrawnGraphs.models;

import java.util.Objects;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

public class WikiDocItemDetails {
  private String label;
  private String description;
  private String QID;

  public WikiDocItemDetails(ItemDocument document) {
    this.label = document.findLabel("en");
    this.description = document.findDescription("en");
    this.QID = document.getEntityId().getId();
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getQID() {
    return QID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    WikiDocItemDetails wdItemDocInfo = (WikiDocItemDetails) o;
    return Objects.equals(label, wdItemDocInfo.label) &&
        Objects.equals(description, wdItemDocInfo.description) &&
        Objects.equals(QID, wdItemDocInfo.QID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, description, QID);
  }

  @Override
  public String toString() {
    return "WikiDocItemDetails{" +
        "label='" + label + '\'' +
        ", description='" + description + '\'' +
        ", QID='" + QID + '\'' +
        '}';
  }

}
