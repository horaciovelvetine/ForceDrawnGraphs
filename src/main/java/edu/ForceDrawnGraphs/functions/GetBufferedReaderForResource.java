package edu.ForceDrawnGraphs.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;

import edu.ForceDrawnGraphs.interfaces.Reportable;

/**
 * This interface provides a method to get a BufferedReader for a given resource name.
 * It extends the Reportable interface.
 */
public interface GetBufferedReaderForResource extends Reportable {

  /**
   * Returns a BufferedReader for the specified resource name.
   * 
   * @param resourceName the name of the resource
   * @return a BufferedReader for the specified resource name
   */
  public default BufferedReader getBufferedReaderForResource(String resourceName) {
    try {
      return new BufferedReader(new InputStreamReader(new ClassPathResource(resourceName).getInputStream()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
