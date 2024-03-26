package edu.ForceDrawnGraphs.commands;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Item;
import edu.ForceDrawnGraphs.models.Page;
import edu.ForceDrawnGraphs.models.Vertex;

@ShellComponent
public class CreateVertices implements Reportable, GetPreparedStmt {
  private JdbcTemplate jdbcTemplate;
  private Set<Item> itemQueue = new HashSet<>();
  private boolean initialSetFetched = false;
  private int itemsImportedOffset = 0;
  private int paginationSize = 100;

  @SuppressWarnings("null")
  public CreateVertices(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @ShellMethod("Scan Item and Page tables and combine to the vertex table")
  public void createVertices() {
    addItemsToQueueList(); // Get something in the Queue to work on...

    while (itemQueue.size() > 0) {
      Item item = itemQueue.iterator().next();
      Page page = getPageByItemId(item.getItemID());
      Vertex vertex = Vertex.createNewVertexFromRecords(item, page);
      // TODO Perform checks
      // - size of the queue
      //TODO Send to db -- ASYNC or BATCH && BOTH

      itemQueue.remove(item);
    }

    print("Items in Queue on finish: " + itemQueue.size());
  }

  private void addItemsToQueueList() {
    if (!initialSetFetched) {
      fetchItems();
      initialSetFetched = true;
    } else {
      fetchNextPage();
    }
  }

  private void fetchItems() {
    String sql = "SELECT * FROM items LIMIT " + paginationSize + " OFFSET " + itemsImportedOffset + ";";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      if (results.next()) {
        do {
          Item item = Item.mapSqlRowSetToItem(results);
          itemQueue.add(item);
        } while (results.next());
        itemsImportedOffset += paginationSize; // Prepare offset for the next iteration
      }
    } catch (Exception e) {
      report(e);
    }
  }

  private void fetchNextPage() {
    String sql = "SELECT * FROM items LIMIT " + paginationSize + " OFFSET " + itemsImportedOffset + ";";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      if (results.next()) {
        do {
          Item item = Item.mapSqlRowSetToItem(results);
          itemQueue.add(item);
        } while (results.next());
        itemsImportedOffset += paginationSize; // Prepare offset for the next iteration
      } else {
        print("No more items to fetch.");
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

}
