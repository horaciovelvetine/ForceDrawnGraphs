package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.ForceDrawnGraphs.models.wikidata.models.EdgeDetails;
import edu.ForceDrawnGraphs.models.wikidata.models.EdgeDetails.SourceType;
import edu.ForceDrawnGraphs.models.wikidata.models.UnknownSnakVisitor;
import edu.ForceDrawnGraphs.models.wikidata.models.SnakDetails;

/**
 * Processes statement details to extract relevant information for graph visualization.
 */
class StmtDetailsProcessor implements Reportable {
  private final UnknownSnakVisitor snakVisitor = new UnknownSnakVisitor();
  private final Statement originalStmt;
  private final SnakDetails mainSnak;
  private final List<SnakDetails[]> qualifiers;

  /**
   * Constructs a processor for a given Wikidata statement.
   *
   * @param statement The Wikidata statement to process.
   */
  public StmtDetailsProcessor(Statement statement) {
    this.originalStmt = statement;
    this.mainSnak = extractMainSnakDetails();
    this.qualifiers = extractQualifierDetails();
  }

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
   * Generates a list of edges based on the processed statement details.
   *
   * @param srcVertex The source vertex for the edges.
   * @return A list of edges derived from the statement details, or null if no relevant edges could be generated.
   */
  public List<Edge> createEdgesFromDetails(Vertex srcVertex) {
    String srcVertexQID = srcVertex.details().QID();
    Edge mainEdge = createEdgeFromSnakDetails(srcVertexQID, mainSnak, SourceType.SNAK_MAIN);
    if (mainEdge == null) {
      return null;
    }

    List<Edge> edges = new ArrayList<>();
    edges.add(mainEdge);

    if (qualifiers != null) {
      for (SnakDetails[] group : qualifiers) {
        for (SnakDetails snak : group) {
          Edge qualifierEdge = createEdgeFromSnakDetails(srcVertexQID, snak, SourceType.SNAK_QUALIFIER);
          if (qualifierEdge != null) {
            qualifierEdge.details().setContextEdge(mainEdge.details());
            edges.add(qualifierEdge);
          }
        }
      }
    }

    return edges.isEmpty() ? null : edges;
  }

  /**
   * Extracts the main Snak details from the original statement.
   *
   * @return The extracted main Snak details.
   */
  private SnakDetails extractMainSnakDetails() {
    return originalStmt.getMainSnak().accept(snakVisitor);
  }

  /**
   * Extracts the qualifier details from the original statement.
   *
   * @return A list of arrays representing grouped qualifier details.
   */
  private List<SnakDetails[]> extractQualifierDetails() {
    return collectSnakGroupedDetails(originalStmt.getQualifiers());
  }

  /**
   * Checks if the Snak's datatype is among the excluded types.
   *
   * @param snak The Snak to check.
   * @return True if the datatype is excluded, false otherwise.
   */
  private boolean isExcludedDataType(SnakDetails snak) {
    return "external-id".equals(snak.datatype()) || "monolingualtext".equals(snak.datatype());
  }

  /**
   * Checks if the Snak's property is among the excluded properties.
   *
   * @param snak The Snak to check.
   * @return True if the property is excluded, false otherwise.
   */
  private boolean isExcludedProperty(SnakDetails snak) {
    String[] excludedProperties = { "P1343", "P143", "P935", "P8687", "P3744", "P18", "P373" };
    return isPropertyInList(snak.property().QID(), excludedProperties);
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

    for (SnakDetails[] group : qualifiers) {
      for (SnakDetails snak : group) {
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
  private List<SnakDetails[]> collectSnakGroupedDetails(List<SnakGroup> groups) {
    if (groups == null) {
      return null;
    }
    List<SnakDetails[]> groupedDetails = new ArrayList<>();

    for (SnakGroup group : groups) {
      SnakDetails[] groupDetails = new SnakDetails[group.size()];
      int index = 0;

      for (Snak snak : group) {
        SnakDetails snakDetails = snak.accept(snakVisitor);
        if (snakDetails != null) {
          groupDetails[index++] = snakDetails;
        }
      }
      groupedDetails.add(groupDetails);
    }

    return groupedDetails.isEmpty() ? null : groupedDetails;
  }

  /**
   * Creates an edge from the given Snak details.
   *
   * @param srcVertexQID The QID of the source vertex.
   * @param snakDetails The details of the Snak to create the edge from.
   * @param edgeSourceType The type of the edge source.
   * @return An Edge object representing the created edge, or null if the creation was skipped.
   */
  private Edge createEdgeFromSnakDetails(String srcVertexQID, SnakDetails snakDetails, SourceType edgeSourceType) {
    EdgeDetails edgeDetails = new EdgeDetails(snakDetails.property(), edgeSourceType);
    String tgtQID = determineTargetQID(snakDetails);

    if (tgtQID == null) {
      System.out.println("StmtDetailsProcessor: Skipped " + snakDetails.value().type() + " TxtValueType: "
          + snakDetails.value().type() + " :equals: " + snakDetails.value().text());
      return null;
    }

    return new Edge(srcVertexQID, tgtQID, edgeDetails);
  }

  /**
   * Determines the target QID based on the value type of the Snak details.
   *
   * @param snakDetails The Snak details to analyze.
   * @return The determined target QID, or null if the value type is not supported.
   */
  private String determineTargetQID(SnakDetails snakDetails) {
    switch (snakDetails.value().type()) {
      case ENTITY:
      case PROPERTY:
        return snakDetails.value().QID();
      case TIME:
        return snakDetails.value().text();
      case STRING:
      case QUANT:
      default:
        return null;
    }
  }
}
