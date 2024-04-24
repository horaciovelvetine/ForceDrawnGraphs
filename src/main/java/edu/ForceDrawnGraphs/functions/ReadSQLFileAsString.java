/**
 * This interface provides a method to read an SQL file as a string.
 * It extends the Reportable interface.
 */
package edu.ForceDrawnGraphs.functions;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public interface ReadSQLFileAsString extends Reportable {
  /**
   * Reads the contents of an SQL file and returns it as a string.
   * 
   * @param sqlFileResourceName The name of the SQL file resource.
   * @return The contents of the SQL file as a string, or null if an error occurs.
   */
  public default String readSQLFileAsString(String sqlFileResourceName) {
    Resource resource = new ClassPathResource(sqlFileResourceName);
    try {
      byte[] sqlFileBytes = resource.getInputStream().readAllBytes();
      String sql = new String(sqlFileBytes);
      resource.getInputStream().close();
      return sql;
    } catch (Exception e) {
      report("@ReadSQLFileAsString.java:", e);
      return null;
    }
  }
}
