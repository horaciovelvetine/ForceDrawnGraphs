package edu.ForceDrawnGraphs.commands;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;

@ShellComponent
public class CreateTables implements ExecuteSQLResourceFile {
  private String sqlPath = "sql/";
  private JdbcTemplate jdbcTemplate;

  @ShellMethod("Create a variety of tables (destructively).")
  public String create(@ShellOption(defaultValue = "no-input") String target) {
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
        return "Please specify a target: edges, hyperlinks, items, pages, properties, statements, vertices, all";
    }

    executeSQL(sqlPath, jdbcTemplate);

    return "doom";
  }
}