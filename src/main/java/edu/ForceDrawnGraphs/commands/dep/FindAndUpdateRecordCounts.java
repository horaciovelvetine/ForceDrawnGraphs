package edu.ForceDrawnGraphs.commands.dep;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.FindTotalRecordsInFile;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.LocalSetInfo;

/**
 * This class is responsible for finding and updating the record counts in each file of the data set.
 */
@ShellComponent
public class FindAndUpdateRecordCounts implements FindTotalRecordsInFile, Reportable {
  private JdbcTemplate jbdcTemplate;
  private LocalSetInfo localSetInfo = new LocalSetInfo();

  /**
   * Constructs a new FindAndUpdateRecordCounts object with the specified data source.
   *
   * @param dataSource the data source used for database operations
   */
  @SuppressWarnings("null")
  public FindAndUpdateRecordCounts(DataSource dataSource) {
    this.jbdcTemplate = new JdbcTemplate(dataSource);
  }

  /**
   * Counts and updates the number of records in each file of the data set.
   */
  @SuppressWarnings("null")
  @ShellMethod("Count and report the number of records in each file of the data set.")
  public void counts() {
    findLocalSetInfo();
    localSetInfo.findRecordTotals();
    try {
      jbdcTemplate.update(localSetInfo.getSQLUpdateQuery());
    } catch (Exception e) {
      report(e);
    }
    report(localSetInfo.toString());
  }

  /**
   * Get the info regarding state of import of local set, and update the local set info object. 
   */
  private void findLocalSetInfo() {
    String sql = "SELECT * FROM local_set_info WHERE id = 1;";
    try {
      SqlRowSet results = jbdcTemplate.queryForRowSet(sql);
      if (results.next()) {
        localSetInfo.mapRowResultsToLocalSetInfo(results);
      }
    } catch (Exception e) {
      report(e);
    }
  }

}
