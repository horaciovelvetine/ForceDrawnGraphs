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

  private Set<Item> itemCache = new HashSet<Item>();
  private Set<Page> pageCache = new HashSet<Page>();

  public GraphsetCommands(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
    this.graphset = new Graphset();
  }

  @ShellMethod
  public void demoset() {
    // String urlEx = "https://en.wikipedia.org/wiki/Byandovan";
    ProcessTimer timer = new ProcessTimer("demoset()");

    // The vertex only requires knowledge of the page to be created (item whic may be null)
    Page page = getRandomPage();
    Item item = getItemByPage(page);
    Vertex genesisVertex = createAndAddVertexToGraphset(page, item); // use return value as 'srcVertexID'

    //! DIVIDER FOR VERTEX VS. EDGE CREATION IN THE PROCESS
    Set<Hyperlink> relatedHyperlinks = new HashSet<Hyperlink>();
    Set<Statement> relatedStatements = new HashSet<Statement>();
    Set<String> relatedPageIDs = new HashSet<String>();
    Set<String> relatedItemIDs = new HashSet<String>();
    // Get and hyperlinks or statements which reference the page or item
    if (page != null) {
      relatedHyperlinks = getHyperlinksByPageID(page.getPageID());
      relatedPageIDs = getUniqueTargetPageIDs(relatedHyperlinks, page.getPageID());
    }

    if (item != null) {
      relatedStatements = getStatementsByItemID(item.getItemID());
      relatedItemIDs = getUniqueTargetItemIDs(relatedStatements, item.getItemID());
    }
    // Check local graphset for existing references to target pages and items
    for (Vertex vertex : graphset.getVertices()) {
      if (relatedPageIDs.contains(vertex.getSrcPageId()) || relatedItemIDs.contains(vertex.getSrcItemId())) {
        relatedPageIDs.remove(vertex.getSrcPageId());
        relatedItemIDs.remove(vertex.getSrcItemId());
      }
    }
    //! DIVIDER FOR RELATED VERTEX CRTATION IN THE PROCESS
    // Create and add vertices for each remaining target page and item
    for (String relatedPageID : relatedPageIDs) {
      Page relatedPage = getPageByID(relatedPageID);
      Item relatedItem = getItemByPage(relatedPage);
      createAndAddVertexToGraphset(relatedPage, relatedItem);
      // relatedPageIDs.remove(relatedPageID); // causes concurrent modification exception
      // ! This is a null return candidate for async execution
    }
    for (String relatedItemID : relatedItemIDs) {
      Item relatedItem = getItemByID(relatedItemID);
      Page relatedPage = getPageByItem(relatedItem);
      createAndAddVertexToGraphset(relatedPage, relatedItem);
      // relatedItemIDs.remove(relatedItemID); // causes concurrent modification exception
      // ! This is a null return candidate for async execution
    }

    print("GOTTA STOP SOMEWHERE");

    timer.end();
  }

  private Page getPageByID(String pageID) {
    ProcessTimer timer = new ProcessTimer("getPageByID(" + pageID + ")");

    String sql = "SELECT * FROM pages WHERE page_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, pageID);
      if (results.next()) {
        return Page.mapSQLRowSetToPage(results);
      }
    } catch (Exception e) {
      report("Error on getPageByID() in Graphset.java:", e);
    } finally {
      timer.end();
    }
    return null;
  }

  private Item getItemByID(String itemID) {
    ProcessTimer timer = new ProcessTimer("getItemByID(" + itemID + ")");

    String sql = "SELECT * FROM items WHERE item_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemID);
      if (results.next()) {
        return Item.mapSqlRowSetToItem(results);
      }
    } catch (Exception e) {
      report("Error on getItemByID() in Graphset.java:", e);
    } finally {
      timer.end();
    }
    return null;
  }

  private Page getRandomPage() {
    ProcessTimer timer = new ProcessTimer("getRandomPage()");

    String sql = "SELECT * FROM pages ORDER BY RANDOM() LIMIT 1;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      if (results.next()) {
        return Page.mapSQLRowSetToPage(results);
      }
    } catch (Exception e) {
      report("Error on getRandomPage() in Graphset.java:", e);
    } finally {
      timer.end();
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

  private Page getPageByItem(Item item) {
    ProcessTimer timer = new ProcessTimer("getPageByItem(" + item.getItemID() + ")");

    String sql = "SELECT * FROM pages WHERE item_id = ?;";
    Page page = null;

    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, item.getItemID());
      if (results.next()) {
        return Page.mapSQLRowSetToPage(results);
      } else {
        report("No page found for item " + item.getItemID());
      }
    } catch (Exception e) {
      report("Error on getPageByItem() in Graphset.java:", e);
    } finally {
      timer.end();
    }
    return page;
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

  private Vertex createAndAddVertexToGraphset(Page page, Item item) {
    ProcessTimer timer = new ProcessTimer("createAndAddVertexToGraphset();");
    Vertex vertex = null;
    if (page == null || item == null) {
      report("No page or item found for vertex creation.");
    }
    if (item != null && page != null) {
      vertex = new Vertex(item, page);
    } else if (item != null) {
      vertex = new Vertex(item);
    } else if (page != null) {
      vertex = new Vertex(page);
    }

    graphset.addVertex(vertex);
    timer.end();
    return vertex;
  }

  private Set<String> getUniqueTargetPageIDs(Set<Hyperlink> hyperlinks, String pageID) {
    ProcessTimer timer = new ProcessTimer("getUniqueTargetPageIDs(" + pageID + ")");
    Set<String> targetPageIDS = new HashSet<String>();
    for (Hyperlink hyperlink : hyperlinks) {
      if (hyperlink.getFromPageID().equals(pageID)) {
        targetPageIDS.add(hyperlink.getToPageID());
      } else {
        targetPageIDS.add(hyperlink.getFromPageID());
      }
    }
    timer.end();
    return targetPageIDS;
  }

  private Set<String> getUniqueTargetItemIDs(Set<Statement> statements, String itemID) {
    ProcessTimer timer = new ProcessTimer("getUniqueTargetItemIDs(" + itemID + ")");
    Set<String> targetItemIDS = new HashSet<String>();
    for (Statement statement : statements) {
      if (statement.getSrcItemID().equals(itemID)) {
        targetItemIDS.add(statement.getTgtItemID());
      } else {
        targetItemIDS.add(statement.getSrcItemID());
      }
    }
    timer.end();
    return targetItemIDS;
  }

}
