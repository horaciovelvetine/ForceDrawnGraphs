package edu.ForceDrawnGraphs.wikidata.models;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;

public record WikiRecSnak(String datatype, WikiRecValue property, WikiRecValue value) {

  public WikiRecSnak(ValueSnakImpl snak, UnknownValueVisitor visitor) {
    this(snak.getDatatype(), snak.getPropertyId().accept(visitor), snak.getValue().accept(visitor));
  }
}
