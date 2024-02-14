package edu.ForceDrawnGraphs.commands;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.sql.DataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.models.LocalSetInfo;
import edu.ForceDrawnGraphs.util.ExecuteSQL;
import edu.ForceDrawnGraphs.util.Reportable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

@ShellComponent
public class BuildLocalSet implements ExecuteSQL, Reportable {
  private DataSource dataSource;
  private JdbcTemplate jdbcTemplate;
  private LocalSetInfo localSetInfo = new LocalSetInfo();
  private BufferedReader bufferedReader;
  private int preparedStatementUpdateTrigger = 10;

  //
  //
  public BuildLocalSet(DataSource dataSource) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
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
      bufferedReader = new BufferedReader(new FileReader(new ClassPathResource("data/item.csv").getFile()));
      String line = bufferedReader.readLine();
      int preparedStatementsCount = 0; // Track the number of prepared statements executed
      PreparedStatement preparedStatement = getPreparedStatement(
          "INSERT INTO items (item_id, en_label, en_description) VALUES (?, ?, ?)");

      while (line != null) {
        //! ENT DATA VAR ASSIGNMENTS
        String[] itemData = getArrayOfStringAttributesFromCSV(line, 3);

        //? Can you skip this? 
        String itemId = itemData[0];
        String enLabel = itemData[1];
        String enDescription = itemData[2];
        //? Ends here? 

        //! UPDATE PREPARED STATEMENT
        preparedStatement.setString(1, itemId);
        preparedStatement.setString(2, enLabel);
        preparedStatement.setString(3, enDescription);

        //! UPDATE THE TRACKING INFO
        preparedStatementsCount++;
        localSetInfo.increment("items");

        //! COMMITS A LONG PREPPED STMNTS OF SET ENT
        if (preparedStatementsCount == preparedStatementUpdateTrigger) {
          // Execute the batch update and reset the counter
          preparedStatement.executeUpdate();
          preparedStatementsCount = 0;
        }
        //! MOVE ON
        line = bufferedReader.readLine();
      }

      //! COMMITS REST OF ENTS
      if (preparedStatementsCount > 0) {
        preparedStatement.executeUpdate();
      }

    } catch (Exception e) {
      //HANDLE FAIL TO IMPORT RECOORDS AND ALL THE OTHER T/C
      report("Error importing item records: " + e.getMessage());
    }
  }

  private PreparedStatement getPreparedStatement(String sql) throws SQLException {
    return dataSource.getConnection().prepareStatement(sql);
  }

  private String[] getArrayOfStringAttributesFromCSV(String line, int numOfAttributesExpected) {
    String[] entData = line.split(",");
    if (entData.length > numOfAttributesExpected) {
      for (int i = 0; i < entData.length; i++) {
        if (i > numOfAttributesExpected - 1) {
          entData[numOfAttributesExpected - 1] += entData[i];
        }
      }
      return Arrays.copyOfRange(entData, 0, numOfAttributesExpected);
    } else {
      return entData;
    }
  }

  //!===========================================================>
  //
  //      FIND OR RUN .SQL MIGRATION
  //
  //!===========================================================>

  /**
   * Finds or creates the local set schema by querying the database.
   */
  private void findOrCreateLocalSetSchema() {
    try {
      SqlRowSet localSetInfoResults = jdbcTemplate.queryForRowSet("SELECT * FROM local_set_info");
      if (localSetInfoResults.next()) {
        localSetInfo.mapRowResultsToLocalSetInfo(localSetInfoResults);
      }
    } catch (Exception e) {
      log(e);
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
