package edu.ForceDrawnGraphs.jung;

import java.awt.Dimension;
import java.util.Date;
import java.util.Random;
import com.google.common.base.Function;

public class RandomLocationTransformer3D<V> implements Function<V, Point3D> {
  private Dimension dimension;
  private Random random;

  public RandomLocationTransformer3D(Dimension dimension) {
    this.dimension = dimension;
    this.random = new Random(new Date().getTime());
  }

  @Override
  public Point3D apply(V input) {
    return new Point3D(random.nextDouble() * dimension.width, random.nextDouble() * dimension.height, random.nextDouble() * dimension.height);
  }
}
