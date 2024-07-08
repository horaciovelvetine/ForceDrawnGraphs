package edu.ForceDrawnGraphs.jung.layouts;

import java.awt.Dimension;
import edu.ForceDrawnGraphs.jung.FRLayout3D;
import edu.ForceDrawnGraphs.jung.Point3D;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class FR3DAdaptiveCooling extends FRLayout3D {

  public FR3DAdaptiveCooling(Graph<Vertex, Edge> graph, Dimension size) {
    super(graph, size);
  }

  @Override
  public void cool() {
    temperature *= (1.0 - currentIteration / (double) maxIterations);
    if (currentIteration % 100 == 0) {
      adjustForceConstants();
    }
  }

  private void adjustForceConstants() {
    double averageRepulsionForce = calculateAverageRepulsionForce();
    double averageAttractionForce = calculateAverageAttractionForce();

    if (averageRepulsionForce > averageAttractionForce) {
      repConst *= 0.9; 
      attrConst *= 1.1; 
    } else if (averageAttractionForce > averageRepulsionForce) {
      repConst *= 1.1; 
      attrConst *= 0.9; 
    }
  }

  private double calculateAverageRepulsionForce() {
    double totalRepulsionForce = 0;
    int count = 0;

    for (Vertex v1 : getGraph().getVertices()) {
      for (Vertex v2 : getGraph().getVertices()) {
        if (v1 != v2) {
          Point3D p1 = getLocationData(v1);
          Point3D p2 = getLocationData(v2);
          double dl = p1.distance(p2);
          double repForce = (repConst * repConst) / dl;
          totalRepulsionForce += repForce;
          count++;
        }
      }
    }

    return totalRepulsionForce / count;
  }

  private double calculateAverageAttractionForce() {
    double totalAttractionForce = 0;
    int count = 0;

    for (Edge e : getGraph().getEdges()) {
      Pair<Vertex> endpoints = getGraph().getEndpoints(e);
      Vertex v1 = endpoints.getFirst();
      Vertex v2 = endpoints.getSecond();
      Point3D p1 = getLocationData(v1);
      Point3D p2 = getLocationData(v2);
      double dl = p1.distance(p2);
      double force = dl * dl / attrConst;
      totalAttractionForce += force;
      count++;
    }

    return totalAttractionForce / count;
  }

}
