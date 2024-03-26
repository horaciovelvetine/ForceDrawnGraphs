package edu.ForceDrawnGraphs.functions;

import java.sql.PreparedStatement;

import edu.ForceDrawnGraphs.interfaces.Reportable;

/**
 * This interface provides a method to add a batch to a prepared statement.
 * It extends the Reportable interface.
 */
public interface AddBatchToStmt extends Reportable {

  /**
   * Adds a batch to the specified prepared statement.
   * 
   * @param stmt the prepared statement to add the batch to
   */
  public default void addBatchToStmt(PreparedStatement stmt) {
    try {
      stmt.addBatch();
    } catch (Exception e) {
      report("@Line XX of AddBatchToStmt().java:", e);
    }
  }
}
