package edu.ForceDrawnGraphs.models;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.models.wikidata.services.FetchQueue;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
public class Graphset implements Reportable {
  private String originQuery; // can be used to find the origin a little bit later...
  private Set<Property> properties; // LocalStore for any properties that are fetched from the API
  private Set<Edge> edges; // In theory these may become a network as the Guava library is integrated
  private Set<Vertex> vertices; // ditto
  private FetchQueue wikiDataFetchQueue; // Queue of ent details to fetch from the Wikidata API

  public Graphset() {
    this.vertices = new HashSet<>();
    this.edges = new HashSet<>();
    this.properties = new HashSet<>();
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
    if (vertexDetailsAlreadyPresent(vertex))
      return;

    vertices.add(vertex);
  }

  /**
   * Adds a property to the Graphset storage.
   * 
   * @param property Property to add to the Graphset storage.
   * 
   */
  public void addPropToLookup(Property property) {
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
  public void addEdgesToLookupAndUpdateQueues(List<Edge> newEdges) {
    for (Edge edge : newEdges) {
      edges.add(edge);
      // add edge details to appropriate fetchQueue for further processing
      // fetchQueue ignores duplicates
      if (edge instanceof WikiDataEdge) {
        wikiDataFetchQueue.addWikiDataEdgeDetails((WikiDataEdge) edge);
      }
    }
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * 
   * The unique aspects of a Vertex will be its ID and Label. Prevents adding already existing vertices to the Graphset. 
   */
  private boolean vertexDetailsAlreadyPresent(Vertex newVertex) {
    // alias for passing in the ID
    return vertexDetailsAlreadyPresent(newVertex.ID());
  }

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
