package edu.ForceDrawnGraphs.jung;

import java.awt.Dimension;

import com.google.common.base.Function;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.uci.ics.jung.graph.Graph;

public interface Layout3D<V, E> extends Function<V, Point3D> {
  void initialize();

  void reset();

  void setGraph(Graph<V, E> graph);

  Graph<V, E> getGraph();

  Point3D transform(Vertex v);

  void setSize(Dimension size);

  Dimension getSize();

  void lock(V v, boolean state);

  boolean isLocked(V v);

  void setLocation(V v, Point3D location);

}
