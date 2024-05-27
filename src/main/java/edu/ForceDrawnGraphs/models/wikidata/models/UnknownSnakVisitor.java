package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class UnknownSnakVisitor implements SnakVisitor<SnakDetails>, Reportable {

  public UnknownSnakVisitor() {
    //Default constructor...
  }

  @Override
  public SnakDetails visit(ValueSnak snak) {
    if (snak instanceof ValueSnakImpl) {
      return new SnakDetails((ValueSnakImpl) snak);
    } else {
      report("Unhandled Snak type @ visit(): " + snak.getClass().getName());
      throw new IllegalArgumentException("Unhandled Snak type @ visit(): " + snak.getClass().getName());
    }
  }

  @Override
  /**
   * Returns null -- info is not relevant to the application.
   */
  public SnakDetails visit(SomeValueSnak snak) {
    log("Some Value Snak found, returning null...");
    return null;
  }

  /**
   * Returns null -- info is not relevant to the application.
   */
  @Override
  public SnakDetails visit(NoValueSnak snak) {
    log("No Value Snak found, returning null...");
    return null;
  }

}
