package edu.ForceDrawnGraphs.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.core.io.ClassPathResource;

import edu.ForceDrawnGraphs.interfaces.Reportable;

@SuppressWarnings("null")
public interface GetBufferedReaderForResource extends Reportable {
  public default BufferedReader getBufferedReaderForResource(String resourceName) {
    try {
      return new BufferedReader(new InputStreamReader(new ClassPathResource(resourceName).getInputStream()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
