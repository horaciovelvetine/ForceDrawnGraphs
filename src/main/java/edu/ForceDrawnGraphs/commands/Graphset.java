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

  // STATE? BUT I HATE THIS BEING HERE
  private int itemsImportedOffset = 0;
  private int paginationSize = 100000;
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
      Page page = getPageByItemId(item.getItemID());
      Vertex vertex = Vertex.createNewVertexFromRecords(item, page);
      addVertextToBatchInsert(vertextInsertStmt, vertex);

      if (itemsInBatch >= 10000) {
        try {
          vertextInsertStmt.executeBatch();
          itemsInserted += itemsInBatch;
          timer.lap();
          itemsInBatch = 0;
        } catch (Exception e) {
          report(e);
        }
      }
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
