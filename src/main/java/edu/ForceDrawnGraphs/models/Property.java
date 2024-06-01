package edu.ForceDrawnGraphs.models;

public class Property {
  private String ID;
  private String label;
  private String description;

  public Property(String ID) {
    this.ID = ID;
  }

  public String ID() {
    return ID;
  }

  public String label() {
    return label;
  }

  public String description() {
    return description;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "WMPropertyEnt{" +
        "QID='" + ID + '\'' +
        ", label='" + label + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
