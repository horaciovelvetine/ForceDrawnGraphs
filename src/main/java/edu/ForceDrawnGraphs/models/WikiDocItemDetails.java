package edu.ForceDrawnGraphs.models;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

/**
 * Represents the details of a WikiDoc item, including its label, description, and QID.
 */
public record WikiDocItemDetails(String label, String description, String QID) {
  /**
   * Constructs a new WikiDocItemDetails object with the specified label, description, and QID.
   *
   * @param label       the label of the WikiDoc item
   * @param description the description of the WikiDoc item
   * @param QID         the QID of the WikiDoc item
   * @throws IllegalArgumentException if any of the fields are null
   */
  public WikiDocItemDetails {
    if (label == null || description == null || QID == null) {
      throw new IllegalArgumentException("All fields must be non-null");
    }
  }

  /**
   * Constructs a new WikiDocItemDetails object based on the provided ItemDocument.
   *
   * @param document the ItemDocument to extract the details from
   */
  public WikiDocItemDetails(ItemDocument document) {
    this(document.findLabel("en"), document.findDescription("en"), document.getEntityId().getId());
  }
}
