package edu.ForceDrawnGraphs.jung;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

public class FRLayout3D<V, E> extends AbstractLayout<V, E> implements IterativeContext {

  private double forceConst;
  private double temperature;
  private double attrConst;
  private double attrMult = 0.75; // default
  private double repConst;
  private double repMult = 0.75; // default
  //ITERATIVE
  private int currentIteration;
  private int maxIterations = 700; // default
  // NUMERICAL
  private double maxDimension;
  private double EPSILON = 0.000001D; // prevent 0/div errors for small/no movement
  //TD: LoadingCache...

  protected FRLayout3D(Graph<V, E> graph) {
    super(graph);
  }

  @Override
  public void initialize() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public void reset() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public void step() {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public boolean done() {
    throw new UnsupportedOperationException("TODO");
  }

}
