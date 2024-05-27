package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;

public class SnakDetails {
  private String datatype;
  private ValueDetails property;
  private ValueDetails value;
  // to get string values from Value objects
  private UnknownValueVisitor visitor = new UnknownValueVisitor();

  public SnakDetails(ValueSnakImpl snak) {
    this.datatype = snak.getDatatype();
    this.property = snak.getPropertyId().accept(visitor);
    this.value = snak.getValue().accept(visitor);
  }

  public String datatype() {
    return datatype;
  }

  public ValueDetails property() {
    return property;
  }

  public ValueDetails value() {
    return value;
  }
}
