package edu.ForceDrawnGraphs.Wikiverse.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import edu.ForceDrawnGraphs.Wikiverse.models.Wikiset;
import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

public class WikisetDao implements Loggable {
  private JdbcTemplate dbConnection;

  public WikisetDao(BasicDataSource connection) {
    this.dbConnection = new JdbcTemplate(connection);
  }

  public void createWikisetTable() {
    String sql = "CREATE TABLE IF NOT EXISTS wikiset ("
        + "id SERIAL PRIMARY KEY AUTO_INCREMENT,"
        + "item_aliases INTEGER,"
        + "items INTEGER,"
        + "link_annotated_text INTEGER,"
        + "pages INTEGER,"
        + "property_aliases INTEGER,"
        + "properties INTEGER,"
        + "statements INTEGER"
        + ");";
    try {
      dbConnection.execute(sql);
    } catch (Exception e) {
      print("Unable to create the wikiset table, see logs.");
      log(e);
    }
  }

  public Wikiset findOrCreateWikiset() {
    String sql = "SELECT * FROM wikiset;";
    try {
      SqlRowSet results = dbConnection.queryForRowSet(sql);
      if (results.next()) {
        return new Wikiset(results);
      } else {
        return createWikiset();
      }
    } catch (Exception e) {
      print("Unable to find or create the wikiset, see logs.");
      log(e);
    }
    return null;
  }

  public Wikiset createWikiset() {
    String sql = "INSERT INTO wikiset (item_aliases, items, link_annotated_text, pages, property_aliases, properties, statements) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?);";
    try {
      dbConnection.update(sql, new Object[] { 0, 0, 0, 0, 0, 0, 0 });
      return findOrCreateWikiset();
    } catch (Exception e) {
      print("Unable to create the wikiset, see logs.");
      log(e);
    }
    return null;
  }
}
