package edu.ForceDrawnGraphs.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

public record WikiRecValue(TXT_VAL_TYPE type, String value, String refUrl) {

  public enum TXT_VAL_TYPE {
    STRING, TIME, ENTITY, QUANT // QUANT is used in {@UnknownValueVisitor}
  }

  public WikiRecValue(EntityIdValue value) {
    // value.getId() ex. "P123 || Q123"
    // value.getIri() ex. "http://www.wikidata.org/entity/P123"
    this(TXT_VAL_TYPE.ENTITY, value.getId(), value.getIri());
  }

  public WikiRecValue(StringValue value) {
    // only one method.getString() is available 
    this(TXT_VAL_TYPE.STRING, value.getString(), null);
  }

  public WikiRecValue(String value) {
    // value input is DateFormatted String (YYYY-MM-DD)
    this(TXT_VAL_TYPE.TIME, value, null);
  }

  /**
   * @return type (TXT VALUE), value, refUrl as strings
   */
  @Override
  public String toString() {
    return "WikiRecValue{" + "type=" + type + ", value='" + value + '\'' + ", refUrl='" + refUrl
        + '\'' + '}';
  }
}
