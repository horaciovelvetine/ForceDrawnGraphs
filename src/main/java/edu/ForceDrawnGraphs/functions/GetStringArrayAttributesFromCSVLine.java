package edu.ForceDrawnGraphs.functions;

import java.util.Arrays;

public interface GetStringArrayAttributesFromCSVLine {

  /**
   * Returns an array of strings from a CSV line.
   * 
   * @param line the line to parse
   * @param numOfExpectedAttributes number of commas to expect in the line to prevent splitting where the last attribute is a longer string description
   * @return an array of strings from the CSV line
   */
  public default String[] getStringArrayAttributesFromCSVLine(String line, int numOfExpectedAttributes) {
    String[] objAttributes = line.split(",");
    if (objAttributes.length > numOfExpectedAttributes) {
      for (int i = 0; i < objAttributes.length - numOfExpectedAttributes; i++) {
        objAttributes[numOfExpectedAttributes - 1] += objAttributes[numOfExpectedAttributes + i];
      }
      return Arrays.copyOfRange(objAttributes, 0, numOfExpectedAttributes);
    }
    return objAttributes;
  };
}
