package edu.ForceDrawnGraphs.models.wikidata.models;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import edu.ForceDrawnGraphs.interfaces.Reportable;

/**
 * Handles visiting unknown values, casting them to a more specific and usable type,
 * before creating and returning a {@link WikiRecValue} object.
 * Some type-specific formatting may be done to prepare the value for display.
 */
public class UnknownValueVisitor implements ValueVisitor<WikiRecValue>, Reportable {

  /**
   * Default constructor.
   */
  public UnknownValueVisitor() {
    // Default constructor
  }

  /**
   * Visits an EntityIdValue and returns a WikiRecValue.
   *
   * @param value the EntityIdValue to visit.
   * @return a WikiRecValue representing the EntityIdValue.
   */
  @Override
  public WikiRecValue visit(EntityIdValue value) {
    return new WikiRecValue(value);
  }

  /**
   * Visits a QuantityValue and returns a WikiRecValue.
   *
   * @param value the QuantityValue to visit.
   * @return a WikiRecValue representing the QuantityValue.
   */
  @Override
  public WikiRecValue visit(QuantityValue value) {
    ItemIdValue unitItem = value.getUnitItemId();
    String url = (unitItem == null) ? "n/a (see property definition)" : unitItem.getIri();
    String text = value.toString();

    return new WikiRecValue(WikiRecValue.TXT_VAL_TYPE.QUANT, text, url);
  }

  /**
   * Visits a StringValue and returns a WikiRecValue.
   *
   * @param value the StringValue to visit.
   * @return a WikiRecValue representing the StringValue.
   */
  @Override
  public WikiRecValue visit(StringValue value) {
    return new WikiRecValue(value);
  }

  /**
   * Visits a TimeValue and returns a WikiRecValue.
   *
   * @param value the TimeValue to visit.
   * @return a WikiRecValue representing the TimeValue.
   */
  @Override
  public WikiRecValue visit(TimeValue value) {
    String timeString = value.toString().replaceAll("\\s*\\(.*\\)", "");
    return new WikiRecValue(timeString);
  }

  /**
   * These value types are not relevant to the application, and will simply be ignored.
   */
  @Override
  public WikiRecValue visit(UnsupportedValue value) {
    return null;
  }

  @Override
  public WikiRecValue visit(GlobeCoordinatesValue value) {
    return null;
  }

  @Override
  public WikiRecValue visit(MonolingualTextValue value) {
    return null;
  }
}
