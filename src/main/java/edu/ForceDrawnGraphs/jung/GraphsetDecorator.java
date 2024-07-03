package edu.ForceDrawnGraphs.jung;

import java.util.Optional;
import java.util.Set;
import java.awt.Dimension;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.ForceDrawnGraphs.util.ProcessTimer;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.ObservableGraph;

public class GraphsetDecorator extends ObservableGraph<Vertex, Edge> implements Reportable {
  private final Graphset graphset;
  private final Dimension graphSize = new Dimension(1600, 900);
  private final FRLayout<Vertex, Edge> layout = new FRLayout<>(this, graphSize);

  public GraphsetDecorator(Graphset graphset) {
    super(new DirectedSparseMultigraph<Vertex, Edge>());
    this.graphset = graphset;
    addCompleteWikidataEnts();
  }

  public void addCompleteWikidataEnts() {
    if (graphset == null) {
      report("addCompleteWikidataEnts()::No graphset to add.");
      return;
    }

    Set<Edge> completeEdges = graphset.getFetchCompleteEdges();
    if (completeEdges == null) {
      report("addCompleteWikidataEnts()::No edges to add.");
      return;
    }

    for (Edge edge : completeEdges) {
      Optional<Pair<Vertex>> endpoints = graphset.getAssociatedVertices(edge);

      if (endpoints.isPresent()) {
        addVertex(endpoints.get().getFirst());
        addVertex(endpoints.get().getSecond());
        addEdge(edge, endpoints.get());
      }
    }
  }

  public void initFR() {
    ProcessTimer timer = new ProcessTimer("FRLayout()::");
    try {
      layout.initialize();
      layout.setRepulsionMultiplier(0.75); //def 0.75
      layout.setAttractionMultiplier(0.75); //def 0.75
      layout.setMaxIterations(700); //def 700
      while (!layout.done()) {
        layout.step();
      }
    } catch (Exception e) {
      report("initFR()::" + e.getMessage());
    }
    timer.end();
  }

  public void useLayoutToSetCoordPosition() {
    initFR();

    for (Vertex vertex : graphset.vertices()) {
      Double x = layout.getX(vertex);
      Double y = layout.getY(vertex);
      vertex.setCoords(x, y);
    }
  }

}
