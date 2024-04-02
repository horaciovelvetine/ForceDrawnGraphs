package edu.ForceDrawnGraphs.commands;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;

@ShellComponent
public class CreateTables implements ExecuteSQLResourceFile {
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("null")
  public CreateTables(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @ShellMethod("Create a variety of tables (destructively).")
  public void create(@ShellOption(defaultValue = "no-input") String target) {
    ProcessTimer timer = new ProcessTimer("createTable(" + target + ") in CreateTables.java");
    switch (target) {
      case "edges":
        sqlRunner("CreateEdges.sql");
        break;
      case "hyperlinks":
      case "links":
        sqlRunner("CreateHyperlinks.sql");
        break;
      case "items":
        sqlRunner("CreateItems.sql");
        break;
      case "pages":
        sqlRunner("CreatePages.sql");
        break;
      case "properties":
        sqlRunner("CreateProperties.sql");
        break;
      case "statements":
        sqlRunner("CreateStatements.sql");
        break;
      case "vertices":
        sqlRunner("CreateVertices.sql");
        break;
      case "all":
        sqlRunner("CreateVertices.sql");
        sqlRunner("CreateItems.sql");
        sqlRunner("CreateHyperlinks.sql");
        sqlRunner("CreateEdges.sql");
        sqlRunner("CreatePages.sql");
        sqlRunner("CreateProperties.sql");
        sqlRunner("CreateStatements.sql");
        break;
      default:
        report("Please specify a target: edges, hyperlinks, items, pages, properties, statements, vertices, all");
    }
    timer.end();
  }

  private void sqlRunner(String sqlFileName) {
    executeSQL("sql/" + sqlFileName, jdbcTemplate);
  }
}