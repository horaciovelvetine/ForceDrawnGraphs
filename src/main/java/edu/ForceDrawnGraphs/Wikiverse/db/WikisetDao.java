package edu.ForceDrawnGraphs.Wikiverse.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import edu.ForceDrawnGraphs.Wikiverse.exceptions.LocalDatabaseConnectionException;
import edu.ForceDrawnGraphs.Wikiverse.exceptions.WikisetCreationException;
import edu.ForceDrawnGraphs.Wikiverse.models.RecordTotals;
import edu.ForceDrawnGraphs.Wikiverse.models.Wikiset;
import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

public class WikisetDao implements Loggable {
  private final JdbcTemplate dbConnection;

  public WikisetDao(JdbcTemplate dbConnection) {
    this.dbConnection = dbConnection;
  }

  public void findOrCreateWikiset() {
    String sql = "SELECT * FROM wikiset";
    try {
      SqlRowSet results = dbConnection.queryForRowSet(sql);
      if (results.next()) {
        print("Existing Wikiset info found...");
      } else {
        print("No wikiset found, creating one...");
        createWikiset();
      }
    } catch (Exception e) {
      log(new LocalDatabaseConnectionException(e.getMessage()));
    }
  }

  private void createWikiset() throws WikisetCreationException {
    String sql = "INSERT INTO wikiset (created_on, updated_on, total_item_alias_records, total_item_records, total_link_annotated_text_records, total_page_records, total_property_alias_records, total_property_records, total_statement_records ) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, 0, 0, 0, 0);";
    try {
      dbConnection.update(sql);
    } catch (Exception e) {
      log(new LocalDatabaseConnectionException(e.getMessage()));
    }
  }

  private Wikiset mapRowToWikiset(SqlRowSet row) {
    Wikiset wikiset = new Wikiset();
    RecordTotals recordTotals = RecordTotals.mapRowSetToRecordTotals(row);
    wikiset.setCreatedOn(row.getTimestamp("created_on"));
    wikiset.setUpdatedOn(row.getTimestamp("updated_on"));
    wikiset.setRecordTotals(recordTotals);
    return wikiset;
  }
}
