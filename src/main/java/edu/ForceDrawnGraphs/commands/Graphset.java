package edu.ForceDrawnGraphs.commands;

import java.sql.PreparedStatement;
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
import edu.ForceDrawnGraphs.models.Page;
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
    // Page page = Page.getRandomAssPage();
    // Item item = Item.getItemById(page.getItemID());
    // Vertex vertex = Vertex.createNewVertexFromRecords(item, page);
    // List<Hyperlink> hyperlinks = Hyperlink.getHyperlinksByPageId(page.getPageID());
    // List<Statements> statements = Statements.getStatementsByItemIds(item.getItemID());
    // PROCESS TO HERE BUILDS A VERTEX AND THEN GETS ALLLLL THE EDGE ITEMS WHICH ARE CONNECTED ON ONE SIDE TO THE VERTEX. 

    // THEN WE NEED TO GET THE OTHER SIDE OF THE EDGE AND BUILD A VERTEX FOR THAT.
    // THEN WE NEED TO BUILD THE EDGE BETWEEN THE TWO VERTICES.
    // List<Ids> vertexIds = new ArrayList<>();
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

  // ! THIS IS A BIG OL DIVIDER VISUALLY SO I CAN FOCUS AND IGNORE MY OLD USELESS CODE
  // ! THIS IS A BIG OL DIVIDER VISUALLY SO I CAN FOCUS AND IGNORE MY OLD USELESS CODE
  // ! THIS IS A BIG OL DIVIDER VISUALLY SO I CAN FOCUS AND IGNORE MY OLD USELESS CODE
  // ! THIS IS A BIG OL DIVIDER VISUALLY SO I CAN FOCUS AND IGNORE MY OLD USELESS CODE
  // ! THIS IS A BIG OL DIVIDER VISUALLY SO I CAN FOCUS AND IGNORE MY OLD USELESS CODE

  // STATE? BUT I HATE THIS BEING HERE
  private int itemsImportedOffset = 0;
  private int paginationSize = 10000;
  private int itemsInBatch = 0;
  private int itemsInserted = 0;

  @ShellMethod("Merge Items and Pages data to create Vertices for the Graphset")
  public void initVerts() {
    ProcessTimer timer = new ProcessTimer("Init-Graphset-Vertices");

    Set<Item> queriedItemsQueue = new HashSet<>();
    PreparedStatement vertextInsertStmt = getPreparedStmt(
        "INSERT INTO vertices (x, y, z, label, en_description, views, src_item_id, src_page_id) VALUES (?,?,?,?,?,?,?,?);",
        dataSource);

    addItemsToQueriedItemsQueue(queriedItemsQueue);
    while (!queriedItemsQueue.isEmpty()) {
      Item item = queriedItemsQueue.iterator().next();
      // Page page = getPageByItemId(item.getItemID());
      Vertex vertex = Vertex.createNewVertexFromRecords(item);
      addVertextToBatchInsert(vertextInsertStmt, vertex);

      if (itemsInBatch >= 1000) {
        try {
          vertextInsertStmt.executeBatch();
          itemsInserted += itemsInBatch;
          timer.lap();
          itemsInBatch = 0;
          addItemsToQueriedItemsQueue(queriedItemsQueue);
        } catch (Exception e) {
          report(e);
        }
      }
      queriedItemsQueue.remove(item);
    }
  }

  private void addItemsToQueriedItemsQueue(Set<Item> queriedItemsQueue) {
    String sql = "SELECT * FROM items LIMIT " + paginationSize + " OFFSET " + itemsImportedOffset + ";";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      boolean itemsFetched = false;
      while (results.next()) {
        Item item = Item.mapSqlRowSetToItem(results);
        queriedItemsQueue.add(item);
        itemsFetched = true;
      }
      if (itemsFetched) {
        itemsImportedOffset += paginationSize; // Prepare offset for the next iteration
      } else {
        print("No more items to fetch."); // Handle case when no more items are available
      }
    } catch (Exception e) {
      report(e);
    }
  }

  private Page getPageByItemId(String itemId) {
    String sql = "SELECT * FROM pages WHERE item_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemId);
      if (results.next()) {
        return Page.mapSQLRowSetToPage(results);
      }
    } catch (Exception e) {
      report(e);
    }
    return null;
  }

  private void addVertextToBatchInsert(PreparedStatement stmt, Vertex vertex) {
    try {
      stmt.setFloat(1, vertex.getX());
      stmt.setFloat(2, vertex.getY());
      stmt.setFloat(3, vertex.getZ());
      stmt.setString(4, vertex.getLabel());
      stmt.setString(5, vertex.getDescription());
      stmt.setString(6, vertex.getViews());
      stmt.setString(7, vertex.getSrcItemId());
      stmt.setString(8, vertex.getSrcPageId());
      stmt.addBatch();
      itemsInBatch++;
    } catch (Exception e) {
      report(e);
    }
  }
}
