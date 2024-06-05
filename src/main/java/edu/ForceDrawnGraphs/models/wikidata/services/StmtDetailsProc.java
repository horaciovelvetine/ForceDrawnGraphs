package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.wikidata.models.UnknownSnakVisitor;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiRecSnak;

/**
 * Represents a single Wikidata Statement sourced from an EntDocument, and processes the details to create Edges. 
 */
class StmtDetailsProcessor implements Reportable {
  private final UnknownSnakVisitor snakVisitor = new UnknownSnakVisitor();
  private final Statement srcStmt;
  // STATEMENT DETAILS COPIED DIRECTLY
  private final WikiRecSnak mainSnak;
  private final List<WikiRecSnak[]> qualifiers;
  // DATA DERIVED FROM STATEMENT DETAILS
  private final List<Edge> edges = new ArrayList<>();

  // EXCLUSION//INCLUSION//TESTING CRITERIA
  private static final Set<String> EXCLUDED_DATA_TYPES = Set.of("external-id", "monolingualtext",
      "commonsMedia", "url", "globe-coordinate", "geo-shape", "wikibase-lexeme");
  private static final Set<String> EXCLUDED_PROPERTIES = Set.of("P1343", "P143", "P935", "P8687",
      "P3744", "P18", "P373", "P856", "P1748", "P21", "P11889", "P1424", "P11527", "P1545");

  /**
   * Constructs a processor for a given Wikidata statement.
   *
   * @param statement The Wikidata statement to process.
   */
  public StmtDetailsProcessor(Statement statement) {
    this.srcStmt = statement;
    this.mainSnak = statement.getMainSnak().accept(snakVisitor);
    this.qualifiers = collectSnakGroupedDetails(statement.getQualifiers());
  }

  /**
   * Returns the list of edges derived from the statement.
   *
   * @return List of edges.
   */
  public List<Edge> edges() {
    return edges;
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

    WikiDataEdge mainEdgeContext = new WikiDataEdge(mainSnak, srcVertexQID);
    edges.add(mainEdgeContext);


    if (qualifiers != null) {
      for (WikiRecSnak[] group : qualifiers) {
        int groupID = 1;

        for (WikiRecSnak snak : group) {
          // Skip excluded qualifier snaks on same critieria as main snak...
          if (isExcludedDataType(snak) || isExcludedProperty(snak)) {
            continue;
          }
          WikiDataEdge qualifierEdge = new WikiDataEdge(snak, mainEdgeContext, groupID);
          if (qualifierEdge != null) {
            edges.add(qualifierEdge);
          }
        }
        groupID++;
      }
    }
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
    return isPropertyInExcludeList(snak.property().value());
  }

  /**
   * Helper method to check if a property ID is in a given list.
   *
   * @param propertyId The property ID to check.
   * @return True if the property ID is found in the list, false otherwise.
   */
  private boolean isPropertyInExcludeList(String propertyId) {
    for (String id : EXCLUDED_PROPERTIES) {
      if (propertyId.equals(id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Collects the details of each Snak in a SnakGroup and returns them as an array of SnakDetails,
   * where each array represents a distinct SnakGroup.
   *
   * @param groups The list of SnakGroups to process.
   * @return A list of arrays containing the collected SnakDetails.
   */
  private List<WikiRecSnak[]> collectSnakGroupedDetails(List<SnakGroup> groups) {
    if (groups == null) {
      return null;
    }
    List<WikiRecSnak[]> groupedDetails = new ArrayList<>();

    for (SnakGroup group : groups) {
      WikiRecSnak[] groupDetails = new WikiRecSnak[group.size()];
      int index = 0;

      for (Snak snak : group) {
        WikiRecSnak snakDetails = snak.accept(snakVisitor);
        if (snakDetails != null) {
          groupDetails[index++] = snakDetails;
        }
      }
      groupedDetails.add(groupDetails);
    }

    return groupedDetails;
  }
}
