package edu.ForceDrawnGraphs.util;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
  * Execute the SQL file at the given resource path.
  * 
  * @param sqlFileResourceName the name of the SQL file in the resources directory
  * 
  * @param jdbc the JdbcTemplate to execute the SQL statements
  * 
  * @implSpec The SQL file must be located in the resources directory.
  */
public interface ExecuteSQL {

  public default void executeSQL(String sqlFileResourceName, JdbcTemplate jdbc)
      throws IOException, IllegalArgumentException {
    if (sqlFileResourceName == null || sqlFileResourceName.isBlank()) {
      throw new IllegalArgumentException("sqlFileResourcePath cannot be null or blank");
    }
    Resource resource = new ClassPathResource("sql/" + sqlFileResourceName);
    byte[] sqlFileBytes = resource.getInputStream().readAllBytes();
    String sql = new String(sqlFileBytes);

    // Splits at semicolons and removes empty lines & whitespaces
    String[] sqlStatements = sql.split(";\\s*\\r?\\n");
    for (String statement : sqlStatements) {
      if (statement.trim().length() > 0) {
        jdbc.execute(statement);
      }
    }
  }
}
