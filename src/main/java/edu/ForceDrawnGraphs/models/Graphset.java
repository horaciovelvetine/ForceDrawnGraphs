package edu.ForceDrawnGraphs.models;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataEdge;
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
   * Adds a property to the Graphset storage.
   * 
   * @param property Property to add to the Graphset storage.
   * 
   */
  public void addPropToLookupAndQueue(Property property) {
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

  /**
   * 
   * The unique aspects of a Vertex will be its ID. Prevents adding already existing vertices to the Graphset. 
   */
  private boolean vertexDetailsAlreadyPresent(String newVertexID) {
    for (Vertex vertex : vertices) {
      if (vertex.ID().equals(newVertexID)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * The unique aspects of a Property will be its ID. Prevents adding already existing properties to the Graphset. 
   */
  private boolean propertyDetailsAlreadyPresent(Property newProperty) {
    for (Property property : properties) {
      if (property.ID().equals(newProperty.ID())) {
        return true;
      }
    }
    return false;
  }

}
