package edu.ForceDrawnGraphs.models.wikidata.records;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

public record ValueSnakRec(String datatype, PropertyIdValue propertyIdValue, Value value) {

  /**
   * Record wrapper to store the relevant details of a ValueSnakImpl: 
   * datatype, PropertyIDValue, Value, and groupID if the snak is a part of group.
   * 
   * @param ValueSnakImpl the ValueSnakImpl to extract the details from
   * @param groupID the groupID of the snak if it is a part of group
   */
  public ValueSnakRec(ValueSnakImpl snak) {
    this(snak.getDatatype(), snak.getPropertyId(), snak.getValue());
  }
}
