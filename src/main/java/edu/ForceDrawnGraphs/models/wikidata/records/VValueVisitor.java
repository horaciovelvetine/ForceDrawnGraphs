package edu.ForceDrawnGraphs.models.wikidata.records;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class VValueVisitor implements ValueVisitor<String>, Reportable {

  @Override
  public String visit(EntityIdValue value) {
    print("Entity ID Value Visitor");
    return null;
  }

  @Override
  public String visit(QuantityValue value) {
    print("Quantity Value Visitor");
    return null;
  }

  @Override
  public String visit(StringValue value) {
    print("String Value Visitor");
    return null;
  }

  @Override
  public String visit(TimeValue value) {
    print("Time Value Visitor");
    return null;
  }

  @Override
  public String visit(UnsupportedValue value) {
    print("Unsupported Value, just in case...");
    return null;
  }

  @Override
  public String visit(GlobeCoordinatesValue value) {
    return null;
  }

  @Override
  public String visit(MonolingualTextValue value) {
    return null;
  }
}
