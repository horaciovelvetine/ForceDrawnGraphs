package edu.ForceDrawnGraphs.Wikiverse.db;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.ForceDrawnGraphs.Wikiverse.exceptions.LocalDatabaseConnectionException;
import edu.ForceDrawnGraphs.Wikiverse.exceptions.WikisetCreationException;
import edu.ForceDrawnGraphs.Wikiverse.models.Wikiset;
import edu.ForceDrawnGraphs.Wikiverse.utils.Loggable;

public class WikisetDao implements Loggable {
  private final JdbcTemplate dbConnection;

  public WikisetDao(JdbcTemplate dbConnection) {
    this.dbConnection = dbConnection;
  }

  public void createWikiset() throws WikisetCreationException {
    Wikiset ws = new Wikiset();

    String sql = "INSERT INTO wikiset (created_on, updated_on, total_item_alias_records, total_item_records, total_link_annotated_text_records, total_page_records, total_property_alias_records, total_property_records, total_statement_records ) VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?);";
    try {
      dbConnection.update(sql, ws.getRecordTotals().getItemAliases(), ws.getRecordTotals().getItems(),
          ws.getRecordTotals().getLinkAnnotatedTexts(), ws.getRecordTotals().getPages(),
          ws.getRecordTotals().getPropertyAliases(), ws.getRecordTotals().getProperties(),
          ws.getRecordTotals().getStatements());
    } catch (Exception e) {
      log(new LocalDatabaseConnectionException(e.getMessage()));
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

}
