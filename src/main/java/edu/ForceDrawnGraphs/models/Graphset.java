package edu.ForceDrawnGraphs.models;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.util.FuzzyStringMatch;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataVertex;
import edu.ForceDrawnGraphs.wikidata.services.FetchQueue;
import edu.ForceDrawnGraphs.wikidata.services.StmtProc;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */

public class Graphset implements Reportable {
  private int N;
  private String originQuery;
  private FetchQueue wikiDataFetchQueue;
  // STORE
  private Set<Property> properties;
  private Set<Edge> edges;
  private Set<Vertex> vertices;

  public Graphset() {
    N = 0; // set-depth as distance in number of edges from origin
    this.vertices = ConcurrentHashMap.newKeySet();
    this.edges = ConcurrentHashMap.newKeySet();
    this.properties = ConcurrentHashMap.newKeySet();
    this.wikiDataFetchQueue = new FetchQueue();
  }

  public void setOriginQuery(String originQuery) {
    this.originQuery = originQuery;
  }

  public String originQuery() {
    return originQuery;
  }

  public Set<Vertex> vertices() {
    return vertices;
  }

  public Set<Edge> edges() {
    return edges;
  }

  public FetchQueue wikiDataFetchQueue() {
    return wikiDataFetchQueue;
  }

  public void iterateNDepth() {
    N++;
  }

  public int depth() {
    return N;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* PUBLIC ADD METHODS - PUBLIC ADD METHODS - PUBLIC ADD METHODS - PUBLIC ADD METHODS - PUBLIC ADD METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------
  /**
   * Adds a vertex to the Graphset storage.
   * 
   * @param vertex Vertex to add to the Graphset storage.
   * 
   */
  public void addVertexToLookup(Vertex vertex) {
    if (vertexDetailsAlreadyPresent(vertex.ID()))
      return;

    vertices.add(vertex);
  }

  /**
   * Adds a property to the Graphset storage, and updates the appropriate vertex with the matching property QID.
   * 
   * @param property Property to add to the Graphset storage.
   * 
   */
  public void addPropToLookup(Property property) {
    propertyLabelMatchesExistingVertex(property);

    if (propertyDetailsAlreadyPresent(property))
      return;

    properties.add(property);
  }

  /**
   * Adds a list of edges to the Graphset storage and updates the appropriate fetchQueue with details from the edges.
   * 
   * @param newEdges List of edges to add to the Graphset storage.
   * 
   */
  public void addEdgesToLookupAndQueue(StmtProc stmt) {
    Edge edge = stmt.msEdge();

    if (edge instanceof WikiDataEdge) {
      wikiDataFetchQueue.addWikiDataEdgeDetails((WikiDataEdge) edge, N);
      edges.add(edge);
    }
  }

  /**
   * Checks all of the edges in the Graphset for a match with the query value, and assigns the dateVertex(QID) to the edge.
   * 
   * @param dateVertex The newley created dateVertex to assign to the edges.
   * @param queryVal The query value used to store and query the WD API (will be equal to value on edge if matches).
   */
  public void assignDateVertexToEdges(WikiDataVertex dateVertex, String queryVal) {
    for (Edge edge : edges) {
      if (isValidWikiDataEdge(edge, queryVal)) {
        ((WikiDataEdge) edge).setTgtVertexID(dateVertex.QID());
      }
    }
  }

  @Override
  public String toString() {
    return "Graphset [N=" + N + ", originQuery=" + originQuery + ", properties=" + properties.size()
        + ", edges=" + edges.size() + ", vertices=" + vertices.size() + ", wikiDataFetchQueue="
        + wikiDataFetchQueue.countALLQueuedValues() + "]";
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  private boolean isValidWikiDataEdge(Edge edge, String queryVal) {
    if (!(edge instanceof WikiDataEdge)) {
      return false;
    }
    WikiDataEdge wikiEdge = (WikiDataEdge) edge;
    return wikiEdge.value() != null && wikiEdge.value().equals(queryVal);
  }

  private boolean vertexDetailsAlreadyPresent(String newVertexID) {
    for (Vertex vertex : vertices) {
      if (vertex.ID().equals(newVertexID)) {
        return true;
      }
    }
    return false;
  }

  private boolean propertyDetailsAlreadyPresent(Property newProperty) {
    for (Property property : properties) {
      if (property.ID().equals(newProperty.ID())) {
        return true;
      }
    }
    return false;
  }

  private void propertyLabelMatchesExistingVertex(Property property) {
    //TODO: Needs a stop and a way stricter match for this to be useful
    List<Vertex> matchedVertices = FuzzyStringMatch.fuzzyMatch(property.label(), vertices, 100);

    print("Stop, this should not be so easy to match.");
    // if (matchedVertices.size() == 1) {
    //   WikiDataVertex vert = (WikiDataVertex) matchedVertices.get(0);
    //   vert.setMatchingPropertyQID(property.ID());
    // } else if (matchedVertices.size() > 1) {
    //   report("Multiple vertices matched for property: " + property.ID());
    // }
  }

}
