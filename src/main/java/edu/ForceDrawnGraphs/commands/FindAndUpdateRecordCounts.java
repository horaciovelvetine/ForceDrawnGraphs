package edu.ForceDrawnGraphs.commands;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.models.LocalSetInfo;
import edu.ForceDrawnGraphs.util.FindTotalRecordsInFile;
import edu.ForceDrawnGraphs.util.Reportable;

@ShellComponent
public class FindAndUpdateRecordCounts implements FindTotalRecordsInFile, Reportable {
  private JdbcTemplate jbdcTemplate;
  private LocalSetInfo localSetInfo;

  public FindAndUpdateRecordCounts(DataSource dataSource) {
    this.jbdcTemplate = new JdbcTemplate(dataSource);
    //? If this looks up the LocalSetInfo obj on init on bean startup is that potentially an issue? 
  }

  @ShellMethod("Count and update the number of records in each file of the data set.")
  public void counts() {
    /*
     * 1) Find LocalSetInfo if exists
     * 2a) If exists call findNumberOfRecordsInFile on each file (on LocalSetInfo object)
     * 2b) If not exists, create new LocalSetInfo object, see 2a
     * 3) Update the LocalSetInfo object in the db with the new values for each file.
     */

    //2a & 3
    updateLocalSetInfo();
  }

  @SuppressWarnings("null")
  private void updateLocalSetInfo() {
    localSetInfo.findRecordTotals();
    try {
      jbdcTemplate.update(localSetInfo.getSQLUpdateQuery());
    } catch (Exception e) {
      report(e);
    }
  }
}
