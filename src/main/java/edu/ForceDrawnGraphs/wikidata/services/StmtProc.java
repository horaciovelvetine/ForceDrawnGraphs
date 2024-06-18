package edu.ForceDrawnGraphs.wikidata.services;

import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.wikidata.models.UnknownSnakVisitor;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.wikidata.models.WikiRecSnak;
import edu.ForceDrawnGraphs.wikidata.models.WikiRecValue.TXT_VAL_TYPE;

/**
 * Represents a single Wikidata Statement sourced from an EntDocument, and processes the details to create Edges. 
 */
public class StmtProc implements Reportable {
  private final UnknownSnakVisitor snakVisitor = new UnknownSnakVisitor();
  private final Statement srcStmt;
  // STATEMENT DETAILS COPIED DIRECTLY
  private final WikiRecSnak mainSnak;
  // DATA DERIVED FROM STATEMENT DETAILS
  private Edge mainSnakEdge;

  // EXCLUSION//INCLUSION//TESTING CRITERIA
  private static final Set<String> EXCLUDED_DATA_TYPES = Set.of("external-id", "monolingualtext",
      "commonsMedia", "url", "globe-coordinate", "geo-shape", "wikibase-lexeme");
  private static final Set<String> EXCLUDED_PROPERTIES = Set.of("P1343", "P143", "P935", "P8687",
      "P3744", "P18", "P373", "P856", "P1748", "P21", "P11889", "P1424", "P11527", "P1545", "P5008",
      "P1889", "P813", "P214", "P213", "P227", "P244", "P268", "P1006", "P1711", "P648", "P1315",
      "P2163", "P3430", "P1015", "P1207", "P1225", "P4823", "P269", "P322", "P1871", "P691",
      "P4342", "P5361", "P2600", "P535", "P8094", "P7293", "P8189", "P950", "P8318", "P1263",
      "P2949", "P7029", "P7699", "P10227", "P409", "P8081", "P7902", "P4619", "P7369", "P3348",
      "P1368", "P11686", "P10832", "P5034", "P1415", "P6058", "P646", "P5869", "P461", "Q109429537",
      "P7452", "Q19478619", "P4666", "P345", "P2604", "P5007", "Q59522350", "Q32351192", "P1011",
      "P8402", "P2959", "P78", "P5323", "P6104");

  /**
   * Constructs a processor for a given Wikidata statement.
   *
   * @param statement The Wikidata statement to process.
   */
  public StmtProc(Statement statement) {
    this.srcStmt = statement;
    this.mainSnak = statement.getMainSnak().accept(snakVisitor);
  }

  public Edge msEdge() {
    return mainSnakEdge;
  }

  /**
   * Determines if the mainSnak contains irrelevant or external information based on predefined criteria.
   *
   * @return True if the statement is considered irrelevant or external, false otherwise.
   */
  public boolean definesIrrelevantOrExternalInfo() {
    return isExcludedDataType(mainSnak) || isExcludedProperty(mainSnak);
  }

  /**
   * Creates edges from the statement details and adds them to the list of edges stored on the stmtDetailsProc itself.
   *
   * @param srcVertexQID The QID of the source vertex for the edges.
   */
  public void createEdgesFromStmtDetails(String srcVertexQID) {
    if (mainSnak.value() != null && mainSnak.value().type() == TXT_VAL_TYPE.ENTITY) {
      if (srcVertexQID == mainSnak.value().value())
        return;
    }
    mainSnakEdge = new WikiDataEdge(mainSnak, srcVertexQID);
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * Checks if the Snak's missing values or the datatype is among the pre-excluded.
   *
   * @param snak The Snak to check.
   * @return True if the datatype is excluded, false otherwise.
   */
  private boolean isExcludedDataType(WikiRecSnak snak) {
    if (snak == null || snak.value() == null || snak.property() == null) {
      return true;
    }
    return EXCLUDED_DATA_TYPES.contains(snak.datatype());
  }

  /**
   * Checks if the Snak's property is among the excluded properties.
   *
   * @param snak The Snak to check.
   * @return True if the property is excluded, false otherwise.
   */
  private boolean isExcludedProperty(WikiRecSnak snak) {
    return (EXCLUDED_PROPERTIES.contains(snak.property().value())
        || EXCLUDED_PROPERTIES.contains(snak.value().value()));
  }


  public String srcStmtDetails() {
    return srcStmt.toString();
  }
}
