package edu.ForceDrawnGraphs.wikidata.models;

import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.wikidata.models.WikiRecValue.TXT_VAL_TYPE;

public class WikiDataEdge extends Edge {
  private String propertyQID;
  private String value; // QID/or str(date, quantity, url... etc)
  private String datatype; //==> .datatype() from the snak

  public WikiDataEdge(WikiRecSnak snak, String srcVertexQID) {
    super(srcVertexQID, findSnakTargetQID(snak));
    this.propertyQID = snak.property().value();
    this.value = findSnakTargetValue(snak);
    this.datatype = snak.datatype();
  }

  public String propertyQID() {
    return propertyQID;
  }

  public String value() {
    return value;
  }

  public String datatype() {
    return datatype;
  }

  @Override
  public String toString() {
    return "[:propertyQID=" + urlPrefixer(propertyQID) + ", value=" + value + ", datatype="
        + datatype + ", tgtVertexQID=" + urlPrefixer(tgtVertexID()) + ", srcVertexQID="
        + urlPrefixer(srcVertexID()) + " :]";
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  private String urlPrefixer(String qid) {
    if (qid == null)
      return null;
    if (qid.startsWith("P")) {
      return "https://www.wikidata.org/wiki/Property:" + qid;
    } else {
      return "https://www.wikidata.org/wiki/" + qid;
    }
  }

  private static String findSnakTargetQID(WikiRecSnak snak) {
    if (snak.value().type() != TXT_VAL_TYPE.ENTITY)
      return null;
    return snak.value().value();
  }

  private static String findSnakTargetValue(WikiRecSnak snak) {
    if (snak.value().type() == TXT_VAL_TYPE.TIME)
      return snak.value().value();
    return null;
  }

}
