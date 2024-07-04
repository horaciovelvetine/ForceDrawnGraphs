package edu.ForceDrawnGraphs.jung;

import java.awt.geom.Point2D;

public class JPoint3D extends Point2D.Double {
  private double x = 0D;
  private double y = 0D;
  private double z = 0D;

  public JPoint3D() {}

  public JPoint3D(double x, double y, double z) {
    setLocation(x, y, z);
  }

  @Override
  public double getX() {
    return x;
  }

  @Override
  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  @Override
  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
    this.z = 0;
  }

  public void setLocation(double x, double y, double z) {
    setLocation(x, y);
    this.z = z;
  }

  public void setLocation(JPoint3D p) {
    setLocation(p.x, p.y, p.z);
  }

  public void setLocation(Point2D p) {
    setLocation(p.getX(), p.getY());
  }

  public void setLocation(Point2D p, double z) {
    setLocation(p.getX(), p.getY(), z);
  }

  public double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double dz = z2 - z1;
    return dx * dx + dy * dy + dz * dz;
  }

  public double distanceSq(double x, double y, double z) {
    return distanceSq(this.x, this.y, this.z, x, y, z);
  }

  public double distanceSq(JPoint3D pt) {
    return distanceSq(x, y, z, pt.x, pt.y, pt.z);
  }

  public double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
    return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
  }

  public double distance(double x, double y, double z) {
    return Math.sqrt(distanceSq(x, y, z));
  }

  public double distance(JPoint3D pt) {
    return Math.sqrt(distanceSq(pt));
  }

  @Override
  public String toString() {
    String c = ", ";
    return "JPoint3D.Double[" + x + c + y + c + z + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JPoint3D) {
      JPoint3D p = (JPoint3D) obj;
      return x == p.x && y == p.y && z == p.z;
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    long bits = java.lang.Double.doubleToLongBits(x);
    int hash = (int) (bits ^ (bits >>> 32));
    hash = 31 * hash + (int) (java.lang.Double.doubleToLongBits(y)
        ^ (java.lang.Double.doubleToLongBits(y) >>> 32));
    hash = 31 * hash + (int) (java.lang.Double.doubleToLongBits(z)
        ^ (java.lang.Double.doubleToLongBits(z) >>> 32));
    return hash;
  }

}
