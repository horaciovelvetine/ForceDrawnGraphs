package edu.ForceDrawnGraphs.commands;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;

@ShellComponent
public class CreateTables implements ExecuteSQLResourceFile {
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("null")
  public CreateTables(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @ShellMethod("Create a variety of tables (destructively).")
  public void create(@ShellOption(defaultValue = "no-input") String target) {
    String sqlPath = "sql/";
    switch (target) {
      case "edges":
        sqlPath += "CreateEdges.sql";
        break;
      case "hyperlinks":
      case "links":
        sqlPath += "CreateHyperlinks.sql";
        break;
      case "items":
        sqlPath += "CreateItems.sql";
        break;
      case "pages":
        sqlPath += "CreatePages.sql";
        break;
      case "properties":
        sqlPath += "CreateProperties.sql";
        break;
      case "statements":
        sqlPath += "CreateStatements.sql";
        break;
      case "vertices":
        sqlPath += "CreateVertices.sql";
        break;
      case "all":
        sqlPath += "CreateAll.sql";
        break;
      // case "appState": OMITTED
      // DEFAULT
      default:
        report("Please specify a target: edges, hyperlinks, items, pages, properties, statements, vertices, all");
    }

    executeSQL(sqlPath, jdbcTemplate);
  }
}