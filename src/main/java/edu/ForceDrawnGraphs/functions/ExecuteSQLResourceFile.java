package edu.ForceDrawnGraphs.functions;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.ForceDrawnGraphs.interfaces.Reportable;

/**
 * This interface provides a method to execute SQL files on the local DB.
 */
@SuppressWarnings("null")
public interface ExecuteSQLResourceFile extends Reportable {

  /**
  * Execute the SQL file at the given resource path.
  * 
  * @param sqlFileResourceName the name of the SQL file in the resources directory
  * 
  * @param jdbc the JdbcTemplate to execute the SQL statements
  * 
  * @implSpec The SQL file must be located in the resources directory.
  */
  public default void executeSQL(String sqlFileResourceName, JdbcTemplate jdbc) {
    Resource resource = new ClassPathResource(sqlFileResourceName);
    try {
      byte[] sqlFileBytes = resource.getInputStream().readAllBytes();
      String sql = new String(sqlFileBytes);
      String[] sqlStatements = sql.split(";\\s*\\r?\\n"); // Splits semicolons & removes empty spaces
      for (String statement : sqlStatements) {
        if (statement.trim().length() > 0) {
          jdbc.execute(statement);
        }
      }
      resource.getInputStream().close();
    } catch (Exception e) {
      report("@ExecuteSQL().java:", e);
    }

  }
}
