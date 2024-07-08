package edu.ForceDrawnGraphs.jung.layouts;

import java.awt.Dimension;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import edu.ForceDrawnGraphs.jung.FRLayout3D;
import edu.ForceDrawnGraphs.jung.Point3D;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class FR3DCacheDistances extends FRLayout3D {

  protected DistanceCache distanceCache;

  public FR3DCacheDistances(Graph<Vertex, Edge> graph, Dimension size) {
    super(graph, size);
    this.distanceCache = new DistanceCache();
  }

  @Override
  protected void calcRepulsion(Vertex v1) {
    Point3D offset = getOffset(v1);
    if (offset == null)
      return;
    offset.setLocation(0, 0, 0); // initialize offset on ea iteration

    try {
      for (Vertex v2 : getGraph().getVertices()) {
        if (v1 == v2 || (isLocked(v1) && isLocked(v2)))
          continue;

        Point3D p1 = getLocationData(v1);
        Point3D p2 = getLocationData(v2);
        if (p1 == null || p2 == null)
          continue;

        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        double dz = p1.getZ() - p2.getZ();
        double dl = distanceCache.getDistance(v1, v2, p1, p2);
        double repForce = (repConst * repConst) / dl;

        if (Double.isNaN(repForce))
          throw new IllegalArgumentException("NaN in repulsion force calculation");

        double xDisp = dx * repForce;
        double yDisp = dy * repForce;
        double zDisp = dz * repForce;

        updateOffset(v1, xDisp, yDisp, zDisp, isLocked(v2)); //if v2 is locked, offset v1 2x
      }
    } catch (ConcurrentModificationException cme) {
      calcRepulsion(v1); // retry
    }
  }

  @Override
  protected void calcAttraction(Edge e) {
    Pair<Vertex> endpoints = getGraph().getEndpoints(e);
    Vertex v1 = endpoints.getFirst();
    Vertex v2 = endpoints.getSecond();
    if (v1 == null || v2 == null || (isLocked(v1) && isLocked(v2)) || v1 == v2) {
      return;
    }

    Point3D p1 = getLocationData(v1);
    Point3D p2 = getLocationData(v2);
    if (p1 == null || p2 == null)
      return;

    double dx = p1.getX() - p2.getX();
    double dy = p1.getY() - p2.getY();
    double dz = p1.getZ() - p2.getZ();
    double dl = distanceCache.getDistance(v1, v2, p1, p2);
    double force = dl * dl / attrConst;

    if (Double.isNaN(force))
      throw new IllegalArgumentException("NaN in attraction force calculation");

    double xDisp = dx * force / dl;
    double yDisp = dy * force / dl;
    double zDisp = dz * force / dl;

    updateOffset(v1, -xDisp, -yDisp, -zDisp, isLocked(v2)); // opposite direction(s)
    updateOffset(v2, xDisp, yDisp, zDisp, isLocked(v1));
  } 

  protected class DistanceCache {
    private Map<Pair<Vertex>, Double> cache;

    public DistanceCache() {
      this.cache = new ConcurrentHashMap<>();
    }

    public double getDistance(Vertex v1, Vertex v2, Point3D p1, Point3D p2) {
      Pair<Vertex> key = new Pair<>(v1, v2);
      return cache.computeIfAbsent(key, k -> calculateDistance(p1, p2));
    }

    private double calculateDistance(Point3D p1, Point3D p2) {
      return Math.max(EPSILON, p1.distance(p2));
    }
  }
}
