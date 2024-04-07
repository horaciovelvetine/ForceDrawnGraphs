package edu.ForceDrawnGraphs.commands;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
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
    // RANDOM PAGE
    Page page = getRandomPage(); // Omit randomPageGetting from process timing.
    // RANDOM PAGE
    ProcessTimer timer = new ProcessTimer("demoset()");

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
    ProcessTimer timer = new ProcessTimer("getItemByPage(" + page.getPageID() + ")");
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
    } finally {
      timer.end();
    }
    return item;
  }

  private Set<Hyperlink> getHyperlinksByPageID(String pageId) {
    ProcessTimer timer = new ProcessTimer("getHyperlinksByPageID(" + pageId + ")");
    String sql = "SELECT * FROM hyperlinks WHERE from_page_id = ? OR to_page_id = ?;";
    Set<Hyperlink> hyperlinks = new HashSet<Hyperlink>();
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, pageId, pageId);
      while (results.next()) {
        hyperlinks.add(Hyperlink.mapSQLRowSetToHyperlink(results));
      }
    } catch (Exception e) {
      report("Error on getHyperlinksByPageID() in Graphset.java:", e);
    } finally {
      timer.end();
    }
    return hyperlinks;
  }

  private Set<Statement> getStatementsByItemID(String itemId) {
    ProcessTimer timer = new ProcessTimer("getStatementsByItemID(" + itemId + ")");
    String sql = "SELECT * FROM statements WHERE source_item_id = ? OR target_item_id = ?;";
    Set<Statement> statements = new HashSet<Statement>();
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemId, itemId);
      while (results.next()) {
        statements.add(Statement.mapSqlRowSetToStatement(results));
      }
    } catch (Exception e) {
      report("Error on getStatementsByItemID() in Graphset.java:", e);
    } finally {
      timer.end();
    }
    return statements;
  }
}
