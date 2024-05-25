package edu.ForceDrawnGraphs.models.wikidata.records;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class VSnakVisitor
    implements SnakVisitor<ValueSnakRec>, Reportable {

  public VSnakVisitor() {
    //Default constructor...
  }

  @Override
  public ValueSnakRec visit(ValueSnak snak) {
    if (snak instanceof ValueSnakImpl) {
      return new ValueSnakRec((ValueSnakImpl) snak);
    } else {
      report("Unhandled Snak type @ visit(): " + snak.getClass().getName());
      throw new IllegalArgumentException("Unhandled Snak type @ visit(): " + snak.getClass().getName());
    }
  }

  @Override
  /**
   * Returns null -- info is not relevant to the application.
   */
  public ValueSnakRec visit(SomeValueSnak snak) {
    report("Some Value Snak found, returning null...");
    return null;
  }

  /**
   * Returns null -- info is not relevant to the application.
   */
  @Override
  public ValueSnakRec visit(NoValueSnak snak) {
    report("No Value Snak found, returning null...");
    return null;
  }

}
