package edu.ForceDrawnGraphs.util;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
  * Execute the SQL file at the given resource path.
  * 
  * @param sqlFileResourcePath the resource path of the SQL file
  * 
  * @implSpec The SQL file must be located in the resources directory.
  */
public interface ExecuteSQL {

  public default void executeSQL(String sqlFileResourcePath) throws IOException {
    Resource resource = new ClassPathResource(sqlFileResourcePath);
    byte[] sqlFileBytes = resource.getInputStream().readAllBytes();
    String sql = new String(sqlFileBytes);

    // Splits at semicolons and removes empty lines & whitespaces
    String[] sqlStatements = sql.split(";\\s*\\r?\\n");
    for (String statement : sqlStatements) {
      if (statement.trim().length() > 0) {
        // Execute the SQL statement
        // ...
      }
    }
  }
}
