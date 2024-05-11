package edu.ForceDrawnGraphs.models.v1;

import java.util.HashSet;
import java.util.Set;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class Graphset implements Reportable {
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

  public Vertex getVertexByHyperlink(Hyperlink link) {
    for (Vertex vertex : this.vertices) {
      if (vertex.getSrcPageID().equals(link.getFromPageID())) {
        return vertex;
      } else if (vertex.getSrcPageID().equals(link.getToPageID())) {
        return vertex;
      } else {
        report("@getVertexByHyperlink() error:" + link.getId());
      }
    }
    return null;
  }

  public Vertex getVertexByStatemtn(Statement statement) {
    for (Vertex vertex : this.vertices) {
      if (vertex.getSrcItemID().equals(statement.getSrcItemID())) {
        return vertex;
      } else if (vertex.getSrcItemID().equals(statement.getTgtItemID())) {
        return vertex;
      } else {
        report("@getVertexByStatemtn() error:" + statement.getId());
      }
    }
    return null;
  }
}
