package edu.ForceDrawnGraphs.models;

import java.util.HashSet;
import java.util.Set;

public class Graphset {
  private Set<Vertex> vertices;
  private Set<Edge> edges;

  public Graphset() {
    this.vertices = new HashSet<>();
    this.edges = new HashSet<>();
  }

  public void addVertex(Vertex vertex) {
    this.vertices.add(vertex);
  }

  public void addEdge(Edge edge) {
    this.edges.add(edge);
  }

  public Set<Vertex> getVertices() {
    return this.vertices;
  }

  public Set<Edge> getEdges() {
    return this.edges;
  }
}
