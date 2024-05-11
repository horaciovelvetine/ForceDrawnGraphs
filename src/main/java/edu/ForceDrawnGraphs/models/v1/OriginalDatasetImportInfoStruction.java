package edu.ForceDrawnGraphs.models.v1;

public class OriginalDatasetImportInfoStruction {
  public final String resourceFileName;
  public final int expectedNumberOfAttributes; // CSV resources only...
  public final String sqlInsertStatement;

  public OriginalDatasetImportInfoStruction(String resourceFileName, int expectedNumberOfAttributes,
      String sqlInsertStatement) {
    this.resourceFileName = resourceFileName;
    this.expectedNumberOfAttributes = expectedNumberOfAttributes;
    this.sqlInsertStatement = sqlInsertStatement;
  }
}
