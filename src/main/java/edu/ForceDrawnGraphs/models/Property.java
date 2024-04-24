package edu.ForceDrawnGraphs.models;

public class Property extends BaseDatasetRecord {
  private String propertyID;
  private String enLabel;
  private String enDescription;
  private int numberOfReferences; // Number of time the statements table references this property

  public Property(int id, String propertyID, String enLabel, String enDescription, int numberOfReferences) {
    super(id); // Explicitly invoke the super constructor
    this.propertyID = propertyID;
    this.enLabel = enLabel;
    this.enDescription = enDescription;
    this.numberOfReferences = numberOfReferences;
  }

  public String getPropertyID() {
    return propertyID;
  }

  public void setPropertyID(String propertyID) {
    this.propertyID = propertyID;
  }

  public String getEnLabel() {
    return enLabel;
  }

  public void setEnLabel(String enLabel) {
    this.enLabel = enLabel;
  }

  public String getEnDescription() {
    return enDescription;
  }

  public void setEnDescription(String enDescription) {
    this.enDescription = enDescription;
  }

  public int getNumberOfReferences() {
    return numberOfReferences;
  }

  public void setNumberOfReferences(int numberOfReferences) {
    this.numberOfReferences = numberOfReferences;
  }
}
