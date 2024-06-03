package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.wikidata.models.UnknownSnakVisitor;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiRecSnak;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge.SNAK_SRC;;

/**
 * Processes statement details to extract relevant information for graph visualization.
 */
class StmtDetailsProcessor implements Reportable {
  private final UnknownSnakVisitor snakVisitor = new UnknownSnakVisitor();
  private final WikiRecSnak mainSnak;
  private final List<WikiRecSnak[]> qualifiers;
  private final List<Edge> edges = new ArrayList<>(); // all edges derived from the statement details
  private static final Set<String> EXCLUDED_DATA_TYPES = Set.of(
      "external-id",
      "monolingualtext",
      "commonsMedia",
      "url",
      "globe-coordinate",
      "geo-shape");
  private static final Set<String> KNOWN_INCLUDED_DATA_TYPES = Set.of(
      "wikibase-item",
      "wikibase-property",
      "wikibase-lexeme", //TODO: These edges are "experimental" and likely to be removed
      "string",
      "quantity",
      "time");
  private static final Set<String> EXCLUDED_PROPERTIES = Set.of(
      "P1343",
      "P143",
      "P935",
      "P8687",
      "P3744",
      "P18",
      "P373",
      "P856");

  /**
   * Constructs a processor for a given Wikidata statement.
   *
   * @param statement The Wikidata statement to process.
   */
  public StmtDetailsProcessor(Statement statement) {
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
   * Determines if the processed statement contains irrelevant or external information based on predefined criteria.
   *
   * @return True if the statement is considered irrelevant or external, false otherwise.
   */
  public boolean definesIrrelevantOrExternalInfo() {
    return mainSnak == null || isExcludedDataType(mainSnak) || isExcludedProperty(mainSnak);
  }

  /**
   * Creates edges from the statement details and adds them to the list of edges stored on the stmtDetailsProc itself.
   *
   * @param srcVertexQID The QID of the source vertex for the edges.
   */
  public void createEdgesFromStmtDetails(String srcVertexQID) {
    WikiDataEdge mainEdgeContext = createEdgeFromSnak(mainSnak, srcVertexQID, null, SNAK_SRC.MAIN_SNAK);

    if (qualifiers != null) {
      for (WikiRecSnak[] group : qualifiers) {
        for (WikiRecSnak snak : group) {
          // Skip excluded qualifier snaks on same critieria as main snak...
          if (isExcludedDataType(snak) || isExcludedProperty(snak)) {
            continue;
          }
          createEdgeFromSnak(snak, srcVertexQID, mainEdgeContext, SNAK_SRC.QUALIFIER);
        }
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
   * Creates an edge from a given Snak and adds it to the list of edges.
   *
   * @param snak The Snak to create an edge from.
   * @param srcVertexQID The source vertex QID.
   * @param contextEdge The context edge, if any.
   * @param edgeSource The source of the edge.
   * @return The created WikiDataEdge.
   */
  private WikiDataEdge createEdgeFromSnak(WikiRecSnak snak, String srcVertexQID, WikiDataEdge contextEdge,
      SNAK_SRC edgeSource) {
    WikiDataEdge edge = null;

    String contextQID = contextEdge != null ? contextEdge.propertyQID() : null;
    String contextValue = contextEdge != null ? contextEdge.tgtVertexQID() : null;

    if (contextEdge != null && contextValue == null) {
      contextValue = contextEdge.value();
    }

    switch (snak.value().type()) {
      case ENTITY:
        edge = new WikiDataEdge(srcVertexQID, snak.value().value(), snak.property().value(), null,
            contextQID, contextValue, edgeSource, snak.datatype());
        break;
      //TODO STRING VALUE PROCESSING
      case STRING:
      case QUANT:
      case TIME:
        edge = new WikiDataEdge(srcVertexQID, null, snak.property().value(), snak.value().value(),
            contextQID, contextValue, edgeSource, snak.datatype());
        break;
    }

    if (edge != null) {
      edges.add(edge);
    }
    return edge;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* ENT VALIDATION METHODS - ENT VALIDATION METHODS - ENT VALIDATION METHODS - ENT VALIDATION METHODS  
  //
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * Checks if the Snak's datatype is among the excluded types.
   *
   * @param snak The Snak to check.
   * @return True if the datatype is excluded, false otherwise.
   */
  private boolean isExcludedDataType(WikiRecSnak snak) {
    if (snak == null) {
      return true;
    }
    //TODO REMOVE AS TESTING CONSISTENTLY SHOWS NO MISSED DATA TYPES
    if (!KNOWN_INCLUDED_DATA_TYPES.contains(snak.datatype()) && !EXCLUDED_DATA_TYPES.contains(snak.datatype())) {
      report("Unknown datatype found:", snak.datatype());
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
    return isPropertyInList(snak.property().value());
  }

  /**
   * Helper method to check if a property ID is in a given list.
   *
   * @param propertyId The property ID to check.
   * @return True if the property ID is found in the list, false otherwise.
   */
  private boolean isPropertyInList(String propertyId) {
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
