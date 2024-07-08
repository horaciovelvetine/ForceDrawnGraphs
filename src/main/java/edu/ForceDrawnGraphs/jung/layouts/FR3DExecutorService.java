package edu.ForceDrawnGraphs.jung.layouts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.Dimension;
import edu.ForceDrawnGraphs.jung.FRLayout3D;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.uci.ics.jung.graph.Graph;

public class FR3DExecutorService extends FRLayout3D {

  private final ExecutorService executorService;

  public FR3DExecutorService(Graph<Vertex, Edge> graph, Dimension size) {
    super(graph, size);
    int numThreads = Runtime.getRuntime().availableProcessors();
    executorService = Executors.newFixedThreadPool(numThreads);
  }

  public void step() {
    currentIteration++;

    // REPULSION
    List<Callable<Void>> repulsionTasks = new ArrayList<>();
    for (Vertex v : getGraph().getVertices()) {
      repulsionTasks.add(() -> {
        calcRepulsion(v);
        return null;
      });
    }
    try {
      executorService.invokeAll(repulsionTasks);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // ATTRACTION
    List<Callable<Void>> attractionTasks = new ArrayList<>();
    for (Edge e : getGraph().getEdges()) {
      attractionTasks.add(() -> {
        calcAttraction(e);
        return null;
      });
    }
    try {
      executorService.invokeAll(attractionTasks);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // POSITION
    for (Vertex v : getGraph().getVertices()) {
      if (isLocked(v))
        continue;
      calcPositions(v);
    }

    cool();
  }
}
