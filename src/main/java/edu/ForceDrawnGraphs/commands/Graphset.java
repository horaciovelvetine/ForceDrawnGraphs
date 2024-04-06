package edu.ForceDrawnGraphs.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.GetPreparedStmt;

import edu.ForceDrawnGraphs.models.Item;
import edu.ForceDrawnGraphs.models.Hyperlink;
import edu.ForceDrawnGraphs.models.Page;
import edu.ForceDrawnGraphs.models.Statement;
import edu.ForceDrawnGraphs.models.Vertex;

@ShellComponent
public class Graphset implements GetPreparedStmt {
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("null")
  public Graphset(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @ShellMethod
  public void demoset() {
    Page page = getRandomPage();
    Item item = getItemByPage(page);
    Vertex vertex = Vertex.createNewVertexFromRecords(item, page);
    Set<Hyperlink> hyperlinks = getHyperlinksByPageID(page.getPageID());
    Set<Statement> statements = getStatementsByItemID(item.getItemID());
    Set<String> otherVertexIds = new HashSet<>();
  }

  private Page getRandomPage() {
    String sql = "SELECT * FROM pages ORDER BY RANDOM() LIMIT 1;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      if (results.next()) {
        return Page.mapSQLRowSetToPage(results);
      }
    } catch (Exception e) {
      report("Error on getRandomPage() in Graphset.java:", e);
    }
    return null;
  }

  private Item getItemByPage(Page page) {
    String sql = "SELECT * FROM items WHERE item_id = ?;";
    Item item = null;

    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, page.getItemID());
      if (results.next()) {
        return Item.mapSqlRowSetToItem(results);
      } else {
        report("No item found for page " + page.getPageID());
      }
    } catch (Exception e) {
      report("Error on getItemByPage() in Graphset.java:", e);
    }
    return item;
  }

  private Set<Hyperlink> getHyperlinksByPageID(String pageId) {
    String sql = "SELECT * FROM hyperlinks WHERE from_page_id = ? OR to_page_id = ?;";
    Set<Hyperlink> hyperlinks = new HashSet<Hyperlink>();
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, pageId, pageId);
      while (results.next()) {
        hyperlinks.add(Hyperlink.mapSQLRowSetToHyperlink(results));
      }
    } catch (Exception e) {
      report("Error on getHyperlinksByPageID() in Graphset.java:", e);
    }
    return hyperlinks;
  }

  private Set<Statement> getStatementsByItemID(String itemId) {
    String sql = "SELECT * FROM statements WHERE source_item_id = ? OR target_item_id = ?;";
    Set<Statement> statements = new HashSet<Statement>();
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemId, itemId);
      while (results.next()) {
        statements.add(Statement.mapSqlRowSetToStatement(results));
      }
    } catch (Exception e) {
      report("Error on getStatementsByItemID() in Graphset.java:", e);
    }
    return statements;
  }
}
