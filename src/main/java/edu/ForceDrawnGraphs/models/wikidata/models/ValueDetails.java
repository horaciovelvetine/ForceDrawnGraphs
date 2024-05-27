package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

/**
 * Record wrapper to store the relevant details of a given entity value type.
 * Each param correlates to a use on a specific type of value, some values may be null. 
 * 
 * @param QID the QID 
 * @param url the URL
 * @param text some blob of text 
 */
public record ValueDetails(String QID, String text, String url, TxtValueType type) {

  public enum TxtValueType {
    STRING, TIME, ENTITY, QUANT, PROPERTY
  }

  public ValueDetails(PropertyIdValue property) {
    this(property.getId(), null, property.getIri(), TxtValueType.PROPERTY);
  }

  public ValueDetails(EntityIdValue value) {
    // value.getId() ex. "P123 || Q123"
    // value.getIri() ex. "http://www.wikidata.org/entity/P123"
    this(value.getId(), null, value.getIri(), TxtValueType.ENTITY);
  }

  //TODO: I dont think there will ever be a QUANT or STRING that is needed, or if it should instead be nulled
  // Didnt null it out,because the info would be lost w/o any context if null was jumped earlier in the process
  // so far: associated webpage link 
  public ValueDetails(StringValue value) {
    // only one method.getString() is available
    this(null, null, value.getString(), TxtValueType.STRING);
  }

  public ValueDetails(String value) {
    // value input is DateFormatted String (YYYY-MM-DD)
    this(null, value, null, TxtValueType.TIME);
  }
}
