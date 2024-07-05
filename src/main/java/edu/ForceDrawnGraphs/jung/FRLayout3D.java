package edu.ForceDrawnGraphs.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.AtomicDouble;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class FRLayout3D<V, E> extends AbstractLayout<V, E> implements IterativeContext, Reportable {
  //PHYSICAL
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
  //STORE
  LoadingCache<V, Point3D> locationData =
      CacheBuilder.newBuilder().build(new CacheLoader<V, Point3D>() {
        public Point3D load(V vertex) throws Exception {
          return new Point3D();
        }
      });

  public FRLayout3D(Graph<V, E> graph, Dimension dimension) {
    super(graph, dimension);
    maxDimension = Math.max(dimension.width, dimension.height);
    set3DInitializer(new RandomLocationTransformer3D<V>(dimension));
    initialize();
  }

  public void set3DInitializer(Function<V, Point3D> initializer) {
    Function<V, Point3D> chain =
        Functions.<V, Point3D, Point3D>compose(new Function<Point3D, Point3D>() {
          public Point3D apply(Point3D input) {
            return (Point3D) input.clone();
          }
        }, initializer); //build func chain
    this.locationData = CacheBuilder.newBuilder().build(CacheLoader.from(chain)); // fill cache with chain return vals (V,Point3D) pairs
    initialized = true; // set flag
  }

  @Override
  @SuppressWarnings("null")
  public void setSize(Dimension size) {
    if (initialized == false) {
      set3DInitializer(new RandomLocationTransformer3D<V>(size));
    }
    // super.setSize(size); //=> modified below to call 3D compatible adjustLocations()
    if (size != null && graph != null) {

      Dimension oldSize = getSize();
      this.size = size;
      initialize();

      if (!oldSize.equals(size) && oldSize != null) {
        adjustLocations(oldSize, size);
      }
    }
    maxDimension = Math.max(size.width, size.height);
  }

  private Point3D getCoordinates(V v) {
    return locationData.getUnchecked(v);
  }

  public Point3D apply3D(V v) {
    return getCoordinates(v);
  }

  @Override
  public void setInitializer(Function<V, Point2D> initializer) {
    throw new UnsupportedOperationException(
        "Should use set3DInitializer() w/ to provide correct Point3D.double Data use.");
  }

  @Override
  public Point2D apply(V v) {
    throw new UnsupportedOperationException("Should use apply3D() for Point3D.double Data use.");
  }

  @Override
  public void reset() {
    doInit();
  }

  @Override
  public void initialize() {
    doInit();
  }

  private void doInit() {
    Graph<V, E> graph = getGraph();
    Dimension d = getSize();
    if (graph != null && d != null) {
      currentIteration = 0;
      temperature = d.getWidth() / 10;
      forceConst = Math.sqrt(d.getHeight() * d.getWidth() / graph.getVertexCount());
      attrConst = forceConst * attrMult;
      repConst = forceConst * repMult;
    }
  }

  @Override
  public synchronized void step() {
    currentIteration++;
    // REPULSION
    while (true) {
      try {
        for (V v : getGraph().getVertices()) {
          calcRepulsion(v);
        }
        break;
      } catch (ConcurrentModificationException cme) {
        // ignore and retry
      }
    }
    // ATTRACTION
    while (true) {
      try {
        for (E e : getGraph().getEdges()) {
          calcAttration(e);
        }
        // calcAttraction(); //=> modified to calcAttraction(E e)
        break;
      } catch (ConcurrentModificationException cme) {
        // ignore and retry
      }
    }
    // POSITION
    while (true) {
      try {
        for (V v : getGraph().getVertices()) {
          calcPositions(v);
        }
        break;
      } catch (ConcurrentModificationException cme) {
        // ignore and retry
      }
    }
    cool();
  }

  protected void cool() {
    temperature *= (1.0 - currentIteration / (double) maxIterations);
  }

  protected synchronized void calcPositions(V v) {
    Point3D vCord = getCoordinates(v);
    if (vCord == null)
      return;

    print("HERE");
    throw new UnsupportedOperationException("Not completely implemented yet.");
    // double deltaLength = Math.max(EPSILON, xyzd.distance(target));
    // // Clone the original coordinates to ensure that modifications do not affect the original
    // Point3D xyzd = new Point3D(vCord.getX(), vCord.getY(), vCord.getZ());
    // Point3D target = apply3D(v); // This should ideally return a new target position

    // double xDisp = (target.getX() - xyzd.getX()) / deltaLength * Math.min(deltaLength, temperature);
    // double yDisp = (target.getY() - xyzd.getY()) / deltaLength * Math.min(deltaLength, temperature);
    // double zDisp = (target.getZ() - xyzd.getZ()) / deltaLength * Math.min(deltaLength, temperature);

    // xyzd.setLocation(xyzd.getX() + xDisp, xyzd.getY() + yDisp, xyzd.getZ() + zDisp);

    // // Boundary checks and adjustments
    // double cWid = getSize().getWidth();
    // double borderWid = cWid / 50.0;

    // double dx = Math.min(Math.max(borderWid, xyzd.getX()), cWid - borderWid);
    // double dy = Math.min(Math.max(borderWid, xyzd.getY()), cWid - borderWid);
    // double dz = Math.min(Math.max(borderWid, xyzd.getZ()), cWid - borderWid);

    // xyzd.setLocation(dx, dy, dz);

    // // Update the actual coordinates with the new position
    // setLocation(v, xyzd);
  }

  protected void calcAttraction() {
    getGraph().getEdges().parallelStream().forEach(e -> {
      Pair<V> endpoints = getGraph().getEndpoints(e);
      V v1 = endpoints.getFirst();
      V v2 = endpoints.getSecond();

      Point3D p1 = getCoordinates(v1);
      Point3D p2 = getCoordinates(v2);

      if (p1 == null || p2 == null)
        return; // Safety check

      double xDelta = p1.getX() - p2.getX();
      double yDelta = p1.getY() - p2.getY();
      double zDelta = p1.getZ() - p2.getZ();

      double deltaLength =
          Math.max(EPSILON, Math.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta));
      double force = (deltaLength * deltaLength) / attrConst;

      double dx = (xDelta / deltaLength) * force;
      double dy = (yDelta / deltaLength) * force;
      double dz = (zDelta / deltaLength) * force;

      synchronized (p1) {
        if (!isLocked(v1)) {
          p1.setLocation(p1.getX() + dx, p1.getY() + dy, p1.getZ() + dz);
        }
      }
      synchronized (p2) {
        if (!isLocked(v2)) {
          p2.setLocation(p2.getX() - dx, p2.getY() - dy, p2.getZ() - dz);
        }
      }
    });
  }


  protected void calcAttration(E e) {
    Pair<V> endpoints = getGraph().getEndpoints(e);
    V v1 = endpoints.getFirst();
    V v2 = endpoints.getSecond();

    Point3D p1 = getCoordinates(v1);
    Point3D p2 = getCoordinates(v2);
    if (Double.isNaN(p1.getX()) || Double.isNaN(p1.getY()) || Double.isNaN(p1.getZ())
        || Double.isNaN(p2.getX()) || Double.isNaN(p2.getY()) || Double.isNaN(p2.getZ())) {
      print("HERE");
      throw new UnsupportedOperationException("NaN values detected in p1 or p2.");
    }
    // occasionally encountering Nan values for mostly p1, unsure origin of issue
    // double xDelta = p1.getX() - p2.getX();
    // double yDelta = p1.getY() - p2.getY();
    // double zDelta = p1.getZ() - p2.getZ();

    // double deltaLength =
    //     Math.max(EPSILON, Math.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta));
    // double force = (deltaLength * deltaLength) / attrConst;

    // double dx = (xDelta / deltaLength) * force;
    // double dy = (yDelta / deltaLength) * force;
    // double dz = (zDelta / deltaLength) * force;

    // if (!isLocked(v1)) {
    //   p1.setLocation(p1.getX() + dx, p1.getY() + dy, p1.getZ() + dz);
    // }
    // if (!isLocked(v2)) {
    //   p2.setLocation(p2.getX() - dx, p2.getY() - dy, p2.getZ() - dz);
    // }
  }


  protected void calcRepulsion(V v1) {
    Point3D origDat = getCoordinates(v1);
    if (origDat == null)
      return;

    AtomicDouble dx = new AtomicDouble(0);
    AtomicDouble dy = new AtomicDouble(0);
    AtomicDouble dz = new AtomicDouble(0);

    getGraph().getVertices().parallelStream().forEach(v2 -> {
      if (v1 != v2) {
        Point3D pDat2 = apply3D(v2);
        if (pDat2 == null)
          return;

        double xDelta = origDat.getX() - pDat2.getX();
        double yDelta = origDat.getY() - pDat2.getY();
        double zDelta = origDat.getZ() - pDat2.getZ();

        double distance = Math.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta);
        double force = (repConst * repConst) / distance;
        if (Double.isNaN(force)) {
          print("HERE");
          throw new UnsupportedOperationException("Not completely implemented yet.");
        }
        // is force.nan() possible?
        dx.addAndGet((xDelta / distance) * force);
        dy.addAndGet((yDelta / distance) * force);
        dz.addAndGet((zDelta / distance) * force);
      }
      origDat.setLocation(origDat.getX() + dx.get(), origDat.getY() + dy.get(),
          origDat.getZ() + dz.get());
    });
  }



  @Override
  public boolean done() {
    if (currentIteration > maxIterations || temperature < 1.0 / maxDimension) {
      return true;
    }
    return false;
  }


  private void adjustLocations(Dimension oldSize, Dimension size) {
    int xOff = (size.width - oldSize.width) / 2;
    int yOff = (size.height - oldSize.height) / 2;
    int zOff = (size.height - oldSize.height) / 2;

    while (true) {
      try {
        for (V v : getGraph().getVertices()) {
          offsetVertex(v, xOff, yOff, zOff);
        }
        break;
      } catch (ConcurrentModificationException cme) {
        // ignore and retry
      }
    }
  }

  protected void offsetVertex(V v, double dx, double dy, double dz) {
    Point3D p = getCoordinates(v);
    double ox = p.getX() + dx;
    double oy = p.getY() + dy;
    double oz = p.getZ() + dz;
    p.setLocation(ox, oy, oz);
  }

  public void setLocation(V v, double x, double y, double z) {
    Point3D p = getCoordinates(v);
    p.setLocation(x, y, z);
  }

  public void setLocation(V v, Point3D p) {
    Point3D q = getCoordinates(v);
    q.setLocation(p);
  }

  public void setRepulsionMultiplier(double d) {
    repMult = d;
  }

  public void setAttractionMultiplier(double d) {
    attrMult = d;
  }

  public void setMaxIterations(int i) {
    maxIterations = i;
  }

}
