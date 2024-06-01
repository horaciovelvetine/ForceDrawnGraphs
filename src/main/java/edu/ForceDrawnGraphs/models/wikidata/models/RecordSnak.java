package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;

public record RecordSnak(String datatype, RecordValue property, RecordValue value) {

  public RecordSnak(ValueSnakImpl snak, UnknownValueVisitor visitor) {
    this(snak.getDatatype(), snak.getPropertyId().accept(visitor), snak.getValue().accept(visitor));
  }
}
