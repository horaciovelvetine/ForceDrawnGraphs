package edu.ForceDrawnGraphs.jung.layouts;

import java.awt.Dimension;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.jung.Layout3D;
import edu.ForceDrawnGraphs.jung.Point3D;
import edu.ForceDrawnGraphs.jung.RandomLocationTransformer3D;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Vertex;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class FR3DLayout implements Layout3D<Vertex, Edge>, IterativeContext, Reportable {
  // PHYSICAL CONSTANTS
  private double attrMult = 0.75; // biasses tendency of vertices to move towards each other
  private double repMult = 0.75; // biasses tendency of vertices to move away from each other
  private double forceConst;
  private double temperature;
  private double attrConst;
  private double repConst;
  private double maxDimension; // a-directional maximum of the layout
  private Dimension size;
  private double EPSILON = 0.000001; // avoid division by zero
  // ITERATIVE CONTEXT
  private boolean initialized = false;
  private int currentIteration;
  private int maxIterations = 700;
  private int iterMvmntMax = 5; // limit on 'unit' movement per iteration
  private int dithMagMult = 2; // randomize position jitter magnitude 
  private double borderWidth;

  // DATA STORAGE
  private Graph<Vertex, Edge> graph;
  private Set<Vertex> lockedVertices = ConcurrentHashMap.newKeySet();
  private DistanceCache distanceCache;
  LoadingCache<Vertex, Point3D> locationData =
      CacheBuilder.newBuilder().build(new CacheLoader<Vertex, Point3D>() {
        public Point3D load(Vertex v) throws Exception {
          return new Point3D();
        }
      });
  LoadingCache<Vertex, Point3D> offsetData =
      CacheBuilder.newBuilder().build(new CacheLoader<Vertex, Point3D>() {
        public Point3D load(Vertex v) throws Exception {
          return new Point3D();
        }
      });

  public FR3DLayout(Graph<Vertex, Edge> graph, Dimension size) {
    if (graph == null || size == null) {
      throw new IllegalArgumentException("Graph and size must be non-null");
    }
    this.graph = graph;
    this.size = size;
    this.distanceCache = new DistanceCache();
    setAndInitPositions(new RandomLocationTransformer3D<Vertex>(size));
    doInit();
  }

  /**
   * Main function of the layout. Given a vertex, returns a set of 3D coordinates.
   */
  public Point3D apply(Vertex v) {
    return getLocationData(v);
  }

  public Point3D transform(Vertex v) {
    return getLocationData(v);
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //* GETTERS AND SETTERS * GETTERS AND SETTERS * GETTERS AND SETTERS * GETTERS AND SETTERS * GETTERS AND SETTERS
  //
  //------------------------------------------------------------------------------------------------------------

  public void setGraph(Graph<Vertex, Edge> graph) {
    this.graph = graph;
    if (size != null && graph != null) {
      initialize();
    }
  }

  public Graph<Vertex, Edge> getGraph() {
    return graph;
  }

  public void setSize(Dimension size) {
    if (initialized == false) {
      setAndInitPositions(new RandomLocationTransformer3D<Vertex>(size));
    }
    if (size != null && graph != null) {

      Dimension oldSize = getSize();
      this.size = size;
      doInit();

      if (!oldSize.equals(size) && oldSize != null) {
        adjustLocations(oldSize, size);
        maxDimension = Math.max(size.width, size.height);
      }
    }
  }

  public Dimension getSize() {
    return size;
  }

  public void setLocation(Vertex v, Point3D location) {
    Point3D coords = getLocationData(v);
    coords.setLocation(location);
  }

  public void setLocation(Vertex v, double x, double y, double z) {
    Point3D coords = getLocationData(v);
    coords.setLocation(x, y, z);
  }

  public void lock(Vertex v, boolean state) {
    if (state) {
      lockedVertices.add(v);
    } else {
      lockedVertices.remove(v);
    }
  }

  public void lock(boolean lock) {
    for (Vertex v : graph.getVertices()) {
      lock(v, lock);
    }
  }

  public boolean isLocked(Vertex v) {
    return lockedVertices.contains(v);
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //!    LAYOUT METHODS * LAYOUT METHODS * LAYOUT METHODS * LAYOUT METHODS * LAYOUT METHODS * LAYOUT METHODS 
  //
  //------------------------------------------------------------------------------------------------------------

  public void initialize() {
    doInit();
  }

  public void reset() {
    doInit();
  }

  private void doInit() {
    Graph<Vertex, Edge> graph = getGraph();
    Dimension d = getSize();
    if (graph != null && d != null) {
      currentIteration = 0;
      temperature = d.getWidth() / 10;
      forceConst = Math.sqrt(d.getHeight() * d.getWidth() / graph.getVertexCount());
      attrConst = forceConst * attrMult;
      repConst = forceConst * repMult;
      maxDimension = Math.max(d.width, d.height);
      borderWidth = Math.min(d.width, d.height) / 50;
    }
  }

  public synchronized void step() {
    currentIteration++;
    getGraph().getVertices().parallelStream().forEach(this::calcRepulsion);
    getGraph().getEdges().parallelStream().forEach(this::calcAttration);
    getGraph().getVertices().parallelStream().filter(v -> !isLocked(v))
        .forEach(this::calcPositions);
    cool();
  }

  public boolean done() {
    if (currentIteration > maxIterations || temperature < 1.0 / maxDimension) {
      return true;
    }
    return false;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //? FORCE CALCS * FORCE CALCS * FORCE CALCS * FORCE CALCS * FORCE CALCS * FORCE CALCS * FORCE CALCS * FORCE
  //
  //------------------------------------------------------------------------------------------------------------

  private void calcRepulsion(Vertex v1) {
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

  private void calcAttration(Edge e) {
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

  private void calcPositions(Vertex v) {
    Point3D p = getLocationData(v);
    if (p == null)
      return;
    Point3D offset = getOffset(v);

    Double dl = Math.max(EPSILON, offset.distanceSq(offset));

    double xDisp = offset.getX() / dl * Math.min(dl, temperature);
    double yDisp = offset.getY() / dl * Math.min(dl, temperature);
    double zDisp = offset.getZ() / dl * Math.min(dl, temperature);

    if (Double.isNaN(xDisp) || Double.isNaN(yDisp) || Double.isNaN(zDisp))
      throw new IllegalArgumentException("NaN in position calculation");

    double newX = p.getX() + Math.max(-iterMvmntMax, Math.min(iterMvmntMax, xDisp));
    double newY = p.getY() + Math.max(-iterMvmntMax, Math.min(iterMvmntMax, yDisp));
    double newZ = p.getZ() + Math.max(-iterMvmntMax, Math.min(iterMvmntMax, zDisp));

    double width = getSize().getWidth();
    double height = getSize().getHeight();

    newX = adjustPositionToBorderBox(newX, width);
    newY = adjustPositionToBorderBox(newY, height);
    newZ = adjustPositionToBorderBox(newZ, Math.min(width, height)); // z min of x, y

    p.setLocation(newX, newY, newZ);
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS * PRIVATE METHODS * PRIVATE METHODS * PRIVATE METHODS * PRIVATE METHODS * PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  private Point3D getLocationData(Vertex V) {
    return locationData.getUnchecked(V);
  }

  private void setAndInitPositions(Function<Vertex, Point3D> initializer) {
    Function<Vertex, Point3D> chain =
        Functions.<Vertex, Point3D, Point3D>compose(new Function<Point3D, Point3D>() {
          public Point3D apply(Point3D input) {
            return (Point3D) input.clone();
          }
        }, initializer); //build func chain 
    this.locationData = CacheBuilder.newBuilder().build(CacheLoader.from(chain));
    this.offsetData = CacheBuilder.newBuilder().build(CacheLoader.from(chain));
    initialized = true; // set flag
  }

  private void adjustLocations(Dimension oldSize, Dimension size) {
    int xOff = (size.width - oldSize.width) / 2;
    int yOff = (size.height - oldSize.height) / 2;
    int zOff = (size.height - oldSize.height) / 2;

    while (true) {
      try {
        for (Vertex v : getGraph().getVertices()) {
          offsetVertexLocation(v, xOff, yOff, zOff);
        }
        break;
      } catch (ConcurrentModificationException cme) {
        // ignore and retry
      }
    }
  }

  private Point3D getOffset(Vertex v) {
    return offsetData.getUnchecked(v);
  }

  private void offsetVertexLocation(Vertex v, double dx, double dy, double dz) {
    Point3D p = getLocationData(v);
    double ox = p.getX() + dx;
    double oy = p.getY() + dy;
    double oz = p.getZ() + dz;
    p.setLocation(ox, oy, oz);
  }

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
      repConst *= 0.9; // decrease repulsion force constant
      attrConst *= 1.1; // increase attraction force constant
    } else if (averageAttractionForce > averageRepulsionForce) {
      repConst *= 1.1; // increase repulsion force constant
      attrConst *= 0.9; // decrease attraction force constant
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
          double dl = distanceCache.getDistance(v1, v2, p1, p2);
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
      double dl = distanceCache.getDistance(v1, v2, p1, p2);
      double force = dl * dl / attrConst;
      totalAttractionForce += force;
      count++;
    }

    return totalAttractionForce / count;
  }

  private void updateOffset(Vertex v, double xDisp, double yDisp, double zDisp,
      boolean opposingIsLocked) {
    Point3D offset = getOffset(v);
    int factor = opposingIsLocked ? 2 : 1;
    offset.setLocation(offset.getX() + factor * xDisp, offset.getY() + factor * yDisp,
        offset.getZ() + factor * zDisp);
  }

  private double adjustPositionToBorderBox(double coordPos, double dimMax) {
    if (coordPos < borderWidth) {
      return borderWidth + Math.random() * dithMagMult * borderWidth;
    } else if (coordPos > dimMax - borderWidth) {
      return dimMax - borderWidth - Math.random() * dithMagMult * borderWidth;
    }
    return coordPos;
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
