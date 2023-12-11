package edu.ForceDrawnGraphs.Wikiverse.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import edu.ForceDrawnGraphs.Wikiverse.exceptions.LocalDatabaseConnectionException;
import edu.ForceDrawnGraphs.Wikiverse.exceptions.WikisetCreationException;
import edu.ForceDrawnGraphs.Wikiverse.models.RecordLineImportProgress;
import edu.ForceDrawnGraphs.Wikiverse.models.RecordTotals;
import edu.ForceDrawnGraphs.Wikiverse.models.Wikiset;
import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

public class WikisetDao implements Loggable {
  private final JdbcTemplate dbConnection;

  public WikisetDao(JdbcTemplate dbConnection) {
    this.dbConnection = dbConnection;
  }

  public Wikiset findOrCreateWikiset() {
    String sql = "SELECT * FROM wikiset";
    try {
      SqlRowSet results = dbConnection.queryForRowSet(sql);
      if (results.next()) {
        print("Existing Wikiset info found...");
        return mapRowToWikiset(results);
      } else {
        print("No wikiset found, creating one...");
        Wikiset ws = createWikiset();
        print("Wikiset created!");
        print(ws.toString());
        return ws;
      }
    } catch (Exception e) {
      log(new LocalDatabaseConnectionException(e.getMessage()));
      return null;
    }
  }

  private Wikiset createWikiset() throws WikisetCreationException {
    String sql = "INSERT INTO wikiset (created_on, updated_on, total_item_alias_records, total_item_records, total_link_annotated_text_records, total_page_records, total_property_alias_records, total_property_records, total_statement_records ) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0, 0, 0, 0, 0);";
    try {
      dbConnection.update(sql);
      return new Wikiset();
    } catch (Exception e) {
      log(new LocalDatabaseConnectionException(e.getMessage()));
      return null;
    }
  }

  public void updateWikiset(Wikiset wikiset) {
    String sql = "UPDATE wikiset SET updated_on = CURRENT_TIMESTAMP, total_item_alias_records = ?, total_item_records = ?, total_link_annotated_text_records = ?, total_page_records = ?, total_property_alias_records = ?, total_property_records = ?, total_statement_records = ? WHERE id = 1";
    try {
      dbConnection.update(sql, wikiset.getRecordTotals().getItemAliases(),
          wikiset.getRecordTotals().getItems(), wikiset.getRecordTotals().getLinkAnnotatedTexts(),
          wikiset.getRecordTotals().getPages(), wikiset.getRecordTotals().getPropertyAliases(),
          wikiset.getRecordTotals().getProperties(), wikiset.getRecordTotals().getStatements());
    } catch (Exception e) {
      log(new LocalDatabaseConnectionException(e.getMessage()));
    }
  }

  private Wikiset mapRowToWikiset(SqlRowSet row) {
    Wikiset wikiset = new Wikiset();
    RecordTotals recordTotals = RecordTotals.mapRowSetToRecordTotals(row);
    RecordLineImportProgress recordLineImportProgress = RecordLineImportProgress
        .mapRowSetToRecordLineImportProgress(row);
    wikiset.setCreatedOn(row.getTimestamp("created_on"));
    wikiset.setUpdatedOn(row.getTimestamp("updated_on"));
    wikiset.setRecordTotals(recordTotals);
    wikiset.setRecordLineImportProgress(recordLineImportProgress);
    return wikiset;
  }
}
