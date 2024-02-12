package edu.ForceDrawnGraphs.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.Buffer;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.models.LocalSetInfo;
import edu.ForceDrawnGraphs.util.ExecuteSQL;
import edu.ForceDrawnGraphs.util.Reportable;
import java.sql.PreparedStatement;

@ShellComponent
public class BuildLocalSet implements ExecuteSQL, Reportable {
  private JdbcTemplate jdbcTemplate;
  private LocalSetInfo localSetInfo;
  private BufferedReader bufferedReader;
  private int preparedStatementUpdateTrigger = 10;

  //
  //
  public BuildLocalSet(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.localSetInfo = new LocalSetInfo();
  }

  /**
   * Builds, or resumes building, the local set.
   */
  @ShellMethod("Builds, or resumes building, the local set.")
  public void build() {
    report("Begin 'build', looking for existing data...");
    findOrCreateLocalSetSchema();
    importItemRecords();
    print("Gotta stop somewhere");
  }

  private void importItemRecords() {
    try {
      bufferedReader = new BufferedReader(new FileReader("data/item.csv"));
      String line = bufferedReader.readLine();
      int preparedStatementsCount = 0; // Track the number of prepared statements executed

      while (line != null) {
        String[] itemData = line.split(",");
        if (itemData.length != 3) {
          report("Error on line " + localSetInfo.getItemsImported() + ": " + line);
          line = bufferedReader.readLine();
          continue;
        }
        String itemId = itemData[0];
        String enLabel = itemData[1];
        String enDescription = itemData[2];

        PreparedStatement preparedStatement = jdbcTemplate.getDataSource().getConnection()
            .prepareStatement("INSERT INTO items (item_id, en_label, en_description) VALUES (?, ?, ?)");
        preparedStatement.setString(1, itemId);
        preparedStatement.setString(2, enLabel);
        preparedStatement.setString(3, enDescription);

        preparedStatement.executeUpdate();
        preparedStatementsCount++;
        localSetInfo.increment("items");

        if (preparedStatementsCount == preparedStatementUpdateTrigger) {
          // Execute the batch update and reset the counter
          jdbcTemplate.getDataSource().getConnection().commit();
          preparedStatementsCount = 0;
        }

        line = bufferedReader.readLine();
      }

      // Commit any remaining prepared statements
      if (preparedStatementsCount > 0) {
        jdbcTemplate.getDataSource().getConnection().commit();
      }

    } catch (Exception e) {
      report("Error importing item records: " + e.getMessage());
    }
  }

  // !COMPLETED METHODS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! END

  /**
   * Finds or creates the local set schema by querying the database.
   */
  private void findOrCreateLocalSetSchema() {
    try {
      SqlRowSet localSetInfoResults = jdbcTemplate.queryForRowSet("SELECT * FROM local_set_info");
      localSetInfo.mapRowResultsToLocalSetInfo(localSetInfoResults);
    } catch (Exception e) {
      handleLocalSetInfoQueryException(e);
    }
  }

  /**
   * Handles the exception that occurs when querying the local set info and no result is found.
   * 
   * @param e the exception that occurred
   */
  private void handleLocalSetInfoQueryException(Exception e) {
    report("No existing data found, running LocalSetSchema.sql to create needed tables");
    try {
      executeSQL("sql/LocalSetSchema.sql", jdbcTemplate);
    } catch (Exception sqlException) {
      handleLocalSetSchemaExecutionException(sqlException);
    }
  }

  /**
   * Handles the exception that occurs when executing the LocalSetSchema.sql script.
   * 
   * @param sqlException the exception that occurred
   */
  private void handleLocalSetSchemaExecutionException(Exception sqlException) {
    report("Error running LocalSetSchema.sql: " + sqlException.getMessage());
  }

}
