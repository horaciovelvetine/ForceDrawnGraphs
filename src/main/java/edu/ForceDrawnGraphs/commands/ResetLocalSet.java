package edu.ForceDrawnGraphs.commands;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.util.ExecuteSQL;
import edu.ForceDrawnGraphs.util.Reportable;

@ShellComponent
public class ResetLocalSet implements Reportable, ExecuteSQL {
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("null")
  public ResetLocalSet(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @ShellMethod("Drop tables and recreate the local set schema.")
  public void reset() {
    report("Begin 'reset', looking for existing data...");
    dropLocalSetSchema();
    createLocalSetSchema();
    report("End 'reset', local set schema reset.");
  }

  private void dropLocalSetSchema() {
    try {
      executeSQL("DropLocalSetSchema.sql", jdbcTemplate);
    } catch (Exception e) {
      report(e);
    }
  }

  private void createLocalSetSchema() {
    try {
      executeSQL("CreateLocalSetSchema.sql", jdbcTemplate);
    } catch (Exception e) {
      report(e);
    }
  }
}
