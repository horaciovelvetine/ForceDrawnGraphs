package edu.ForceDrawnGraphs.jung;

import java.util.Optional;
import java.util.Set;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Vertex;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.ObservableGraph;

public class GraphsetDecorator extends ObservableGraph<Vertex, Edge> implements Reportable {
  private final Graphset graphset;

  public GraphsetDecorator(Graphset graphset) {
    super(new DirectedSparseMultigraph<Vertex, Edge>());
    this.graphset = graphset;
  }

  public void addCompleteWikidataEnts() {
    Set<Edge> completeEdges = graphset.getCompleteWikidataEnts();

    for (Edge edge : completeEdges) {
      Optional<Pair<Vertex>> endpoints = graphset.getAssociatedVertices(edge);

      if (endpoints.isPresent()) {
        addVertex(endpoints.get().getFirst());
        addVertex(endpoints.get().getSecond());
        //TODO - This should be a weighted edge?
        addEdge(edge, endpoints.get());
      }
    }
  }

}
