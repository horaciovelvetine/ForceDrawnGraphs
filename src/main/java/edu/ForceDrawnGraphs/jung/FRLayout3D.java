package edu.ForceDrawnGraphs.jung;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

public class FRLayout3D<V, E> extends AbstractLayout<V, E> implements IterativeContext {
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


  public FRLayout3D(Graph<V, E> graph) {
    super(graph);
  }

  public FRLayout3D(Graph<V, E> graph, Dimension dimension) {
    super(graph, dimension);
    set3DInitializer(new RandomLocationTransformer3D<V>(dimension));
    maxDimension = Math.max(dimension.width, dimension.height);
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
          calcAttraction(e);
        }
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
    // The two ref issue. 
    if (vCord == null)
      return;
    
  }

  protected void calcAttraction(E e) {}

  protected void calcRepulsion(V v1) {}

  @Override
  public boolean done() {
    //TODO => Review optional squaring of maxDimension here for comparison
    if (currentIteration >= maxIterations || temperature < 1.0 / maxDimension) {
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

}
