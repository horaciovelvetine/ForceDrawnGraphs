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
 * Handles visiting unknown values, casting them to a more specific and usable type, before creating and returning an {@link ValueDetails} object. Some type specific formatting may be done to prepare the value for display.
 */
public class UnknownValueVisitor implements ValueVisitor<ValueDetails>, Reportable {
  public UnknownValueVisitor() {
    //Default constructor...
  }

  @Override
  public ValueDetails visit(EntityIdValue value) {
    return new ValueDetails(value);
  }

  @Override
  public ValueDetails visit(QuantityValue value) {
    ItemIdValue unitItem = value.getUnitItemId();
    String QID;
    String url;
    String text;

    if (unitItem == null) {
      QID = null;
      url = "No unit item found.";
    } else {
      QID = unitItem.getId();
      url = unitItem.getIri();
    }
    text = value.toString();

    return new ValueDetails(QID, url, text, ValueDetails.TxtValueType.QUANT);
  }

  @Override
  public ValueDetails visit(StringValue value) {
    return new ValueDetails(value);
  }

  @Override
  public ValueDetails visit(TimeValue value) {
    // removes any additional information in the string
    // leaving only YYYY-MM-DD([THH:MM:SSZ] => is tbd)
    String timeString = value.toString().replaceAll("\\s*\\(.*\\)", "");
    return new ValueDetails(timeString);
  }

  @Override
  public ValueDetails visit(UnsupportedValue value) {
    print("Unsupported Value, just in case...");
    return null;
  }

  @Override
  public ValueDetails visit(GlobeCoordinatesValue value) {
    return null;
  }

  @Override
  public ValueDetails visit(MonolingualTextValue value) {
    return null;
  }

}
