package edu.ForceDrawnGraphs.functions;

import java.sql.PreparedStatement;

import javax.sql.DataSource;

import edu.ForceDrawnGraphs.interfaces.Reportable;

/**
 * This interface provides a method to get a prepared statement for executing SQL queries.
 * It extends the Reportable interface.
 */
public interface GetPreparedStmt extends Reportable {

  /**
   * Returns a prepared statement for the given SQL query using the provided data source.
   *
   * @param sql the SQL query string
   * @param dataSource the data source to retrieve the connection from
   * @return the prepared statement for executing the SQL query
   */
  public default PreparedStatement getPreparedStmt(String sql, DataSource dataSource) {
    try {
      return dataSource.getConnection().prepareStatement(sql);
    } catch (Exception e) {
      report("@line 25 of GetPreparedStmt().java:", e);
    }
    return null;
  }
}
