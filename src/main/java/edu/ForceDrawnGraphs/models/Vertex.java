package edu.ForceDrawnGraphs.models;

public class Vertex {
  private String ID;
  private String label;
  private double x = 0.0;
  private double y = 0.0;
  private double z = 0.0;

  public Vertex(String ID, String label) {
    this.ID = ID;
    this.label = label;
  }

  public String ID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public String label() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Vertex other = (Vertex) obj;
    return ID == other.ID && Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0
        && Double.compare(z, other.z) == 0;
  }

  @Override
  public int hashCode() {
    int result = 17;
    long xBits = Double.doubleToLongBits(x);
    long yBits = Double.doubleToLongBits(y);
    long zBits = Double.doubleToLongBits(z);
    result = 31 * result;
    result = 31 * result + (int) (xBits ^ (xBits >>> 32));
    result = 31 * result + (int) (yBits ^ (yBits >>> 32));
    result = 31 * result + (int) (zBits ^ (zBits >>> 32));
    return result;
  }

}
