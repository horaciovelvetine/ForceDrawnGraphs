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
  private DataSource dataSource;
  private JdbcTemplate jdbcTemplate;

  @SuppressWarnings("null")
  public Graphset(DataSource datasource) {
    this.dataSource = datasource;
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @ShellMethod
  public void demoset() {
    Page page = getRandomPage();
    Item item = getItemByPage(page);
    Vertex vertex = Vertex.createNewVertexFromRecords(item, page);
    List<Hyperlink> hyperlinks = getHyperlinksByPageID(page.getPageID());
    List<Statement> statements = getStatementsByItemID(item.getItemID());

    Set<String> otherVertexIds = new HashSet<>();
    // For each hyperlink, get the OTHER page (non-current page) and build a vertex for it.
    for (Hyperlink hyperlink : hyperlinks) {
      String otherPageId = hyperlink.getFromPageID();
    }
    
    // for (Hyperlink hyperlink : hyperlinks) { // For each hyperlink, get the other page and build a vertex for it.
    //   Page otherPage = Page.getPageById(hyperlink.getTo_page_id());
    //   Item otherItem = Item.getItemById(otherPage.getItemID());
    //   Vertex otherVertex = Vertex.createNewVertexFromRecords(otherItem, otherPage);
    //   vertexIds.add(otherVertex.getVertexID());
    // }
    // for (Statement statement : statements) { // For each statement, get the other item and build a vertex for it.
    //   Item otherItem = Item.getItemById(statement.getOtherItemId());
    //   Page otherPage = Page.getPageByItemId(otherItem.getItemID());
    //   Vertex otherVertex = Vertex.createNewVertexFromRecords(otherItem, otherPage);
    //   vertexIds.add(otherVertex.getVertexID());
    // }
    // for (Id vertexId : vertexIds) { // For each vertex, build an edge between the two vertices.
    //   Edge edge = new Edge(vertex.getVertexID(), vertexId);
    //   edge.save();
    // }
  }

  private Page getRandomPage() {
    String sql = "SELECT * FROM pages ORDER BY RANDOM() LIMIT 1;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      if (results.next()) {
        return Page.mapSQLRowSetToPage(results);
      }
    } catch (Exception e) {
      report(e);
    }
    return null;
  }

  private Item getItemByPage(Page page) {
    String sql = "SELECT * FROM items WHERE item_id = ?;";

    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, page.getItemID());
      if (results.next()) {
        return Item.mapSqlRowSetToItem(results);
      }
    } catch (Exception e) {
      report(e);
    }
    return null;
  }

  private List<Hyperlink> getHyperlinksByPageID(String pageId) {
    String sql = "SELECT * FROM hyperlinks WHERE from_page_id = ? OR to_page_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, pageId, pageId);
      List<Hyperlink> hyperlinks = new ArrayList<>();
      while (results.next()) {
        hyperlinks.add(Hyperlink.mapSQLRowSetToHyperlink(results));
      }
      return hyperlinks;
    } catch (Exception e) {
      report(e);
    }
    return null;
  }

  private List<Statement> getStatementsByItemID(String itemId) {
    String sql = "SELECT * FROM statements WHERE source_item_id = ? OR target_item_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemId, itemId);
      List<Statement> statements = new ArrayList<>();
      while (results.next()) {
        statements.add(Statement.mapSqlRowSetToStatement(results));
      }
      return statements;
    } catch (Exception e) {
      report(e);
    }
    return null;
  }
}
