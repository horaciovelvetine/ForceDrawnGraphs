package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

public record RecordValue(TxtValueType type, String value, String refUrl) {

  public enum TxtValueType {
    STRING, TIME, ENTITY, QUANT // QUANT is used in {@UnknownValueVisitor}
  }

  public RecordValue(EntityIdValue value) {
    // value.getId() ex. "P123 || Q123"
    // value.getIri() ex. "http://www.wikidata.org/entity/P123"
    this(TxtValueType.ENTITY, value.getId(), value.getIri());
  }

  public RecordValue(StringValue value) {
    // only one method.getString() is available 
    this(TxtValueType.STRING, value.getString(), null);
  }

  public RecordValue(String value) {
    // value input is DateFormatted String (YYYY-MM-DD)
    this(TxtValueType.TIME, value, null);
  }
}
