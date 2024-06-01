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
 * Handles visiting unknown values, casting them to a more specific and usable type, before creating and returning an {@link RecordValue} object. Some type specific formatting may be done to prepare the value for display.
 */
public class UnknownValueVisitor implements ValueVisitor<RecordValue>, Reportable {
  public UnknownValueVisitor() {
    //Default constructor...
  }

  @Override
  public RecordValue visit(EntityIdValue value) {
    return new RecordValue(value);
  }

  @Override
  public RecordValue visit(QuantityValue value) {
    ItemIdValue unitItem = value.getUnitItemId();
    String url; // refUrl
    String text; // value

    // see if a unit item is available, or leave a default message in its place
    if (unitItem == null) {
      url = "n/a (see property definition)";
    } else {
      url = unitItem.getIri();
    }
    text = value.toString();

    return new RecordValue(RecordValue.TxtValueType.QUANT, text, url);
  }

  @Override
  public RecordValue visit(StringValue value) {
    return new RecordValue(value);
  }

  @Override
  public RecordValue visit(TimeValue value) {
    // removes any additional information in the string
    // leaving only YYYY-MM-DD([THH:MM:SSZ] => is tbd)
    String timeString = value.toString().replaceAll("\\s*\\(.*\\)", "");
    return new RecordValue(timeString);
  }

  @Override
  public RecordValue visit(UnsupportedValue value) {
    return null;
  }

  @Override
  public RecordValue visit(GlobeCoordinatesValue value) {
    return null;
  }

  @Override
  public RecordValue visit(MonolingualTextValue value) {
    return null;
  }

}
