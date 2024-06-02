package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;
import java.util.List;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.models.wikidata.services.FetchQueue;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
public class Graphset implements Reportable {
  private String originQuery; // can be used to find the origin a little bit later...
  // private ArrayList<Property> properties; // LocalStore for any properties that are fetched from the API
  private ArrayList<Edge> edges; // In theory these may become a network as the Guava library is integrated
  private ArrayList<Vertex> vertices; // ditto
  private FetchQueue wikiDataFetchQueue; // Queue of ent details to fetch from the Wikidata API

  public Graphset() {
    this.vertices = new ArrayList<>();
    this.edges = new ArrayList<>();
    // this.properties = new ArrayList<>();
    this.wikiDataFetchQueue = new FetchQueue();
  }

  public void setOriginQuery(String originQuery) {
    this.originQuery = originQuery;
  }

  public String originQuery() {
    return originQuery;
  }

  public ArrayList<Vertex> vertices() {
    return vertices;
  }

  public ArrayList<Edge> edges() {
    return edges;
  }

  public FetchQueue wikiDataFetchQueue() {
    return wikiDataFetchQueue;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------
  /**
   * Adds a vertex to the Graphset storage.
   * 
   * @param vertex Vertex to add to the Graphset storage.
   * 
   */
  public void addVertex(Vertex vertex) {
    if (!vertices.contains(vertex))
      vertices.add(vertex);
  }

  /**
   * Adds a list of edges to the Graphset storage and updates the appropriate fetchQueue with details from the edges.
   * 
   * @param newEdges List of edges to add to the Graphset storage.
   * 
   */
  public void addEdgesAndUpdateQueues(List<Edge> newEdges) {
    for (Edge edge : newEdges) {
      // add edge to Graphset storage
      edges.add(edge);
      // add edge details to appropriate fetchQueue for further processing
      if (edge instanceof WikiDataEdge) {
        wikiDataFetchQueue.addWikiDataEdgeDetailsToQueue((WikiDataEdge) edge);
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

}
