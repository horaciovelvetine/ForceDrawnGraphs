package edu.ForceDrawnGraphs.commands;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.functions.ReadSQLFileAsString;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.Item;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Hyperlink;
import edu.ForceDrawnGraphs.models.Page;
import edu.ForceDrawnGraphs.models.Statement;
import edu.ForceDrawnGraphs.models.Vertex;

@ShellComponent
public class GraphsetCommands implements GetPreparedStmt, ReadSQLFileAsString {
  private JdbcTemplate jdbcTemplate;
  private Graphset graphset;

  @SuppressWarnings("null")
  public GraphsetCommands(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @ShellMethod
  public void demoset() {
    ProcessTimer timer = new ProcessTimer("demoset()");

    // The vertex only requires knowledge of the page to be created (item whic may be null)
    Page page = getRandomPage();
    Item item = getItemByPage(page);
    Vertex vertex = createAndAddVertex(page, item);

    // But then the info from a vertex is needed to create the edges - but this can be an independant process.
    // Potentially this vertex can have a variety of edges but some of that data has to also be overlapping
    // The goal is to have 0 overlapping edges. Each edge should be unique and not have any other edge with the same source and target
    // Instead any overlapping edge information should be indicated on the edge itself.
    // Edge object will definitley need to be re-examined at this point. 
    Set<Hyperlink> hyperlinks = getHyperlinksByPageID(page.getPageID());
    Set<Statement> statements = getStatementsByItemID(item.getItemID());

    timer.end();
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

  private Vertex createAndAddVertex(Page page, Item item) {
    Vertex vertex = Vertex.createNewVertexFromRecords(item, page);
    graphset.addVertex(vertex);
    return vertex;
  }

}
