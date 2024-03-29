package edu.ForceDrawnGraphs.commands;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;

@ShellComponent
public class ResetLocalSet implements ExecuteSQLResourceFile {
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("null")
  public ResetLocalSet(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @ShellMethod("Drop tables and recreate the local set schema.")
  public void reset() {
    ProcessTimer processTimer = new ProcessTimer("ResetLocalSet.reset()");
    dropLocalSetSchema();
    createLocalSetSchema();
    processTimer.end();
  }

  private void dropLocalSetSchema() {
    try {
      executeSQL("sql/DropLocalSetSchma.sql", jdbcTemplate);
    } catch (Exception e) {
      report(e);
    }
  }

  private void createLocalSetSchema() {
    try {
      executeSQL("sql/LocalSetSchema.sql", jdbcTemplate);
    } catch (Exception e) {
      report(e);
    }
  }
}
