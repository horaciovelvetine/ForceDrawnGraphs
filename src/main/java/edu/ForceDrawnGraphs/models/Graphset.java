package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;

import edu.ForceDrawnGraphs.models.wikidata.services.FetchQueue;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
public class Graphset {
  private String originQuery; // can be used to find the origin a little bit later...
  private ArrayList<Edge> edges; // In theory these may become a network as the Guava library is integrated
  private ArrayList<Vertex> vertices; // ditto
  private FetchQueue fetchQueue;

  public Graphset() {
    this.vertices = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.fetchQueue = new FetchQueue();
  }

  public void setOriginQuery(String originQuery) {
    this.originQuery = originQuery;
  }

  public String getOriginQuery() {
    return originQuery;
  }

  public ArrayList<Vertex> getVertices() {
    return vertices;
  }

  public ArrayList<Edge> getEdges() {
    return edges;
  }

  public FetchQueue getFetchQueue() {
    return fetchQueue;
  }

  public void addVertex(Vertex vertex) {
    vertices.add(vertex);
  }
}
