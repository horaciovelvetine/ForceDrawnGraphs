package edu.ForceDrawnGraphs.jung;

import java.util.Optional;
import java.util.Set;
import java.awt.Dimension;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.jung.layouts.FR3DAdaptiveCooling;
import edu.ForceDrawnGraphs.jung.layouts.FR3DCacheDistances;
import edu.ForceDrawnGraphs.jung.layouts.FR3DCachedCool;
import edu.ForceDrawnGraphs.jung.layouts.FR3DExecutorService;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.ForceDrawnGraphs.util.ProcessTimer;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.ObservableGraph;

public class GraphsetDecorator extends ObservableGraph<Vertex, Edge> implements Reportable {
  private final Graphset graphset;
  private final Dimension graphSize = new Dimension(1600, 900);
  private final FRLayout<Vertex, Edge> layout = new FRLayout<>(this, graphSize);
  private final FRLayout2<Vertex, Edge> layout2 = new FRLayout2<>(this, graphSize);
  private final FRLayout3D layout3D = new FRLayout3D(this, graphSize);
  private final FR3DExecutorService layout3DCachedCool = new FR3DExecutorService(this, graphSize);

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
        Vertex v1 = endpoints.get().getFirst();
        Vertex v2 = endpoints.get().getSecond();
        if (v1.QID() == v2.QID()) {
          continue;
        }
        if (!containsVertex(v1)) {
          addVertex(v1);
        }
        if (!containsVertex(v2)) {
          addVertex(v2);
        }
        addEdge(edge, endpoints.get());
      }
    }
  }

  public void initFR() {
    ProcessTimer timer = new ProcessTimer("FRLayout()::");
    try {
      layout.initialize();
      while (!layout.done()) {
        layout.step();
      }
    } catch (Exception e) {
      report("initFR()::" + e.getMessage());
    }
    timer.end();
  }

  public void initFR2() {
    ProcessTimer timer = new ProcessTimer("FRLayout2()::");
    try {
      layout2.initialize();
      while (!layout2.done()) {
        layout2.step();
      }
    } catch (Exception e) {
      report("initFR2()::" + e.getMessage());
    }
    timer.end();
  }

  public void initFR3D() {
    ProcessTimer timer = new ProcessTimer("FRLayout3D()::");
    try {
      layout3D.initialize();
      while (!layout3D.done()) {
        layout3D.step();
      }
    } catch (Exception e) {
      report("initFR3D()::" + e.getMessage());
    }
    timer.end();
  }

  public void initFR3DES() {
    ProcessTimer timer = new ProcessTimer("FR3DExecutorService()::");
    try {
      layout3DCachedCool.initialize();
      while (!layout3DCachedCool.done()) {
        layout3DCachedCool.step();
      }
    } catch (Exception e) {
      report("initFR3DCC()::" + e.getMessage());
    }
    timer.end();
  }
}
