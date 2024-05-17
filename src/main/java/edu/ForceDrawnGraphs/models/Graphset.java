package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
public class Graphset {
  private String originQuery;
  private ArrayList<Edge> edges; // In theory these may become a network as the Guava library is integrated
  private ArrayList<Vertex> vertices; // ditto
  private WikiDocFetchQueue fetchQueue;


  public Graphset() {
    this.vertices = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.fetchQueue = new WikiDocFetchQueue();
  }

  public String getOriginQuery() {
    return originQuery;
  }

  public void setOriginQuery(String originQuery) {
    this.originQuery = originQuery;
  }

  public ArrayList<Vertex> getVertices() {
    return vertices;
  }

  public void addVertex(Vertex vertex) {
    vertices.add(vertex);
  }

  public ArrayList<Edge> getEdges() {
    return edges;
  }

  public WikiDocFetchQueue getFetchQueue() {
    return fetchQueue;
  }

  public void setFetchQueue(WikiDocFetchQueue fetchQueue) {
    this.fetchQueue = fetchQueue;
  }
}
