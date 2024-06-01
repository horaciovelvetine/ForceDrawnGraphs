package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.wikidata.models.UnknownSnakVisitor;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiMainSnakEdge;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiQualifierEdge;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiRecSnak;

/**
 * Processes statement details to extract relevant information for graph visualization.
 */
class StmtDetailsProcessor implements Reportable {
  private final UnknownSnakVisitor snakVisitor = new UnknownSnakVisitor();
  // VALUES READ FROM IMPORT PROCESS 
  // private final Statement zsrcStmtStr;
  private final WikiRecSnak mainSnak;
  private final List<WikiRecSnak[]> qualifiers;
  // VALUES SET FROM IMPORT PROCESS
  private List<Edge> edges = new ArrayList<>(); // all edges derived from the statement 

  /**
   * Constructs a processor for a given Wikidata statement.
   *
   * @param statement The Wikidata statement to process.
   */
  public StmtDetailsProcessor(Statement statement) {
    // this.zsrcStmtStr = statement; // for debugging
    this.mainSnak = statement.getMainSnak().accept(snakVisitor);
    this.qualifiers = collectSnakGroupedDetails(statement.getQualifiers());
  }

  public List<Edge> edges() {
    return edges;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //* PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * Determines if the processed statement contains irrelevant or external information based on predefined criteria.
   *
   * @return True if the statement is considered irrelevant or external, false otherwise.
   */
  public boolean definesIrrelevantOrExternalInfo() {
    return mainSnak == null || isExcludedDataType(mainSnak) || isExcludedProperty(mainSnak)
        || hasExcludedQualifierProperties();
  }

  /**
    * Creates edges from the statement details and adds them to the list of edges stored on the stmtDetailsProc itself.
   */
  public void createEdgesFromStmtDetails(String srcVertexQID) {
    //TODO - Edges Creation Refactor

    WikiMainSnakEdge edge = null;

    switch (mainSnak.value().type()) {
      case ENTITY:
        edge = new WikiMainSnakEdge(srcVertexQID, mainSnak.value().value(), mainSnak.property().value(),
            null);
        break;
      case STRING:
      case QUANT:
      case TIME:
        edge = new WikiMainSnakEdge(srcVertexQID, null, mainSnak.property().value(),
            mainSnak.value().value());
        break;

      default:
        break;
    }

    edges.add(edge);

    for (WikiRecSnak[] group : qualifiers) {
      for (WikiRecSnak snak : group) {
        WikiQualifierEdge newEdge = null;

        switch (snak.value().type()) {
          case ENTITY:
            newEdge = new WikiQualifierEdge(srcVertexQID, snak.value().value(), edge,
                snak.property().value(), null);
            break;
          case STRING:
          case QUANT:
          case TIME:
            newEdge = new WikiQualifierEdge(srcVertexQID, null, edge,
                snak.property().value(), snak.value().value());
        }
        edges.add(newEdge);
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
   * Checks if the Snak's datatype is among the excluded types.
   *
   * @param snak The Snak to check.
   * @return True if the datatype is excluded, false otherwise.
   */
  private boolean isExcludedDataType(WikiRecSnak snak) {
    return "external-id".equals(snak.datatype()) || "monolingualtext".equals(snak.datatype());
  }

  /**
   * Checks if the Snak's property is among the excluded properties.
   *
   * @param snak The Snak to check.
   * @return True if the property is excluded, false otherwise.
   */
  private boolean isExcludedProperty(WikiRecSnak snak) {
    String[] excludedProperties = { "P1343", "P143", "P935", "P8687", "P3744", "P18", "P373", "P856" };
    return isPropertyInList(snak.property().value(), excludedProperties);
  }

  /**
   * Checks if any qualifier properties are among the excluded properties.
   *
   * @return True if any qualifier property is excluded, false otherwise.
   */
  private boolean hasExcludedQualifierProperties() {
    if (qualifiers == null) {
      return false;
    }

    for (WikiRecSnak[] group : qualifiers) {
      for (WikiRecSnak snak : group) {
        if (isExcludedProperty(snak)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Helper method to check if a property ID is in a given list.
   *
   * @param propertyId The property ID to check.
   * @param propertyList The list of property IDs to compare against.
   * @return True if the property ID is found in the list, false otherwise.
   */
  private boolean isPropertyInList(String propertyId, String[] propertyList) {
    for (String id : propertyList) {
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
