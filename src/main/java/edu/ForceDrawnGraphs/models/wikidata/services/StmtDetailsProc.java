package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
import java.util.ArrayList;

import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.ForceDrawnGraphs.models.wikidata.models.EdgeDetails;
import edu.ForceDrawnGraphs.models.wikidata.models.EdgeDetails.SourceType;
import edu.ForceDrawnGraphs.models.wikidata.models.ValueDetails.TxtValueType;
import edu.ForceDrawnGraphs.models.wikidata.models.UnknownSnakVisitor;
import edu.ForceDrawnGraphs.models.wikidata.models.SnakDetails;

class StmtDetailsProcessor implements Reportable {
  // visitors
  private UnknownSnakVisitor snakVisitor = new UnknownSnakVisitor();

  // actual values
  private Statement originalStmt;
  private SnakDetails mainSnak;
  private List<SnakDetails[]> qualifiers;

  public StmtDetailsProcessor(Statement statement) {
    this.originalStmt = statement;
    this.mainSnak = setedgeDetails();
    this.qualifiers = setQualifierDetails();
  }

  /**
   * Returns true if the Statement defines some sort of information sourced externally from Wikidata using one of the following criteria:
   * <ul>
     * <li> mainSnak is null</li>
     * <li> mainSnak's datatype is "external-id"</li>
     * <li> mainSnak's datatype is "monolingualtext"</li>
     * <li> mainSnak property is part of an internal exclusion list</li>
     * <li> qualifier properties are part of an internal exclusion list</li>
   * </ul>
   * 
   * @see (docs/PROPERTY_EXCLUSION_LIST.md) for a list of excluded properties
   */
  public boolean definesIrrelevantOrExternalInfo() {
    if (mainSnak == null)
      return true;
    if (mainSnak.datatype().equals("external-id"))
      // external-id defines a subject - irrelevant
      return true;
    if (mainSnak.datatype().equals("monolingualtext"))
      // text defines how to sepll a subject - irrelevant
      return true;
    if (externallySourcedProperty(mainSnak))
      // known list of irrelevant properties
      return true;
    if (externallySourcedProperty(qualifiers))
      // qualifiers are irrelevant properties
      return true;
    else
      return false;
  }

  /**
   * Creates a list of edges from the details of the Statement.
   * 
   * @param srcVertex the source vertex of the edges
   * @return a list of edges created from the details of the Statement
   */
  public List<Edge> createEdgesFromDetails(Vertex srcVertex) {
    List<Edge> createdEdges = new ArrayList<>();
    String srcVertexQID = srcVertex.details().QID();

    // create the main edge
    Edge mainEdge = createEdgeFromSnakDetails(srcVertexQID, mainSnak, EdgeDetails.SourceType.SNAK_MAIN);
    createdEdges.add(mainEdge);

    // create the qualifier edges (if any)
    if (qualifiers != null) {
      for (SnakDetails[] group : qualifiers) {
        for (SnakDetails snak : group) {
          Edge qualifierEdge = createEdgeFromSnakDetails(srcVertexQID, snak, EdgeDetails.SourceType.SNAK_QUALIFIER);
          qualifierEdge.details().setContextEdge(mainEdge.details());
          createdEdges.add(qualifierEdge);
        }
      }
    }

    return createdEdges.isEmpty() ? null : createdEdges;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private Edge createEdgeFromSnakDetails(String srcVertexQID, SnakDetails snakDetails, SourceType edgeSourceType) {
    EdgeDetails edgeDetails = new EdgeDetails(snakDetails.property(), edgeSourceType);
    String tgtQID = null;

    switch (snakDetails.value().type()) {
      case TxtValueType.ENTITY:
      case TxtValueType.PROPERTY:
        tgtQID = snakDetails.value().QID();
        break;
      case TxtValueType.TIME:
        edgeDetails.setValueTgt(snakDetails.value().text());
        break;
      default:
        //todo handle other valueTarget Types: string/quant
        print("Other txt values not yet handled.");
        break;
    }

    return new Edge(srcVertexQID, tgtQID, edgeDetails);

  }

  /**
   * Checks if the property of the suspected Snak is part of an internal black list of externally sourced properties.
   */
  private boolean externallySourcedProperty(SnakDetails suspectedSnak) {
    String[] extPropQIDBlacklist = { "P1343", "P143", "P935", "P8687", "P3744", "P18", "P373" };
    for (String qid : extPropQIDBlacklist) {
      if (suspectedSnak.property().QID().equals(qid)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the property of the suspected List is part of an internal black list of externally sourced properties.
   */
  private boolean externallySourcedProperty(List<SnakDetails[]> suspectedList) {
    if (suspectedList != null) {
      for (SnakDetails[] snakGroup : suspectedList) {
        for (SnakDetails snak : snakGroup) {
          if (externallySourcedProperty(snak)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Collects the details of the main Snak of the original statement.
   */
  private SnakDetails setedgeDetails() {
    return originalStmt.getMainSnak().accept(snakVisitor);
  }

  /**
   * Collects the details of the qualifiers of the original statment.
   */
  private List<SnakDetails[]> setQualifierDetails() {
    return collectSnakGroupedDetails(originalStmt.getQualifiers());
  }

  /**
   * Collects the details of each Snak in a SnakGroup and returns them as an array of ValueSnakRecs,
   * where each array represents a distinct SnakGroup.
   */
  private List<SnakDetails[]> collectSnakGroupedDetails(List<SnakGroup> groups) {
    List<SnakDetails[]> snakGroupedDetails = new ArrayList<>();

    for (SnakGroup group : groups) {
      SnakDetails[] groupDetails = new SnakDetails[group.size()];

      for (Snak snak : group) {
        SnakDetails snakDetails = snak.accept(snakVisitor);
        if (snakDetails != null) {
          groupDetails[group.getSnaks().indexOf(snak)] = snakDetails;
        }
      }
      snakGroupedDetails.add(groupDetails);
    }

    return snakGroupedDetails.isEmpty() ? null : snakGroupedDetails;
  }

}