package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class UnknownSnakVisitor implements SnakVisitor<WikiRecSnak>, Reportable {
  private UnknownValueVisitor valueVisitor = new UnknownValueVisitor();

  public UnknownSnakVisitor() {
    //Default constructor...
  }

  @Override
  public WikiRecSnak visit(ValueSnak snak) {
    if (snak instanceof ValueSnakImpl) {
      return new WikiRecSnak((ValueSnakImpl) snak, valueVisitor);
    } else {
      report("Unhandled Snak type @ visit(): " + snak.getClass().getName());
      throw new IllegalArgumentException(
          "Unhandled Snak type @ visit(): " + snak.getClass().getName());
    }
  }

  @Override
  /**
   * Returns null -- info is not relevant to the application.
   */
  public WikiRecSnak visit(SomeValueSnak snak) {
    return null;
  }

  @Override
  public WikiRecSnak visit(NoValueSnak snak) {
    return null;
  }

}
