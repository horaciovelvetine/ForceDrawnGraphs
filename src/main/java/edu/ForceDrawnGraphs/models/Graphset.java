package edu.ForceDrawnGraphs.models;

import java.util.List;

public class Graphset {
  private List<Vertex> vertices;
  private List<Edge> edges;

  public Graphset(List<Vertex> vertices, List<Edge> edges) {
    this.vertices = vertices;
    this.edges = edges;
  }

  public List<Vertex> getVertices() {
    return vertices;
  }

  public List<Edge> getEdges() {
    return edges;
  }
}
