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
  private int batchSizeUpdateTrigger = 10;
  private int sampleSizeLimit = 100;

  /**
   * Constructor for BuildLocalSet.
   * 
   * @param dataSource configured in application.properties for the database
   */
  @SuppressWarnings("null")
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

    //TODO: Add commenting and reporting at each step.
    //TODO: Pick a level of operation to report at and implement it throughout.

    //TODO: Add reporting for each commit and batch (timestamp each one, for graphing)

    importDatasetRecordsFromFile("item.csv", 3,
        "INSERT INTO items (item_id, en_label, en_description, line_ref) VALUES (?, ?, ?, ?)");
    print("Gotta stop somewhere");
  }

  /**
   * Imports dataset records from a file into the database.
   * 
   * @param resourceName            the name of the resource file
   * @param numOfAttributesExpected the number of expected attributes in each record
   * @param sql                     the SQL statement for inserting records
   */
  private void importDatasetRecordsFromFile(String resourceName, int numOfAttributesExpected, String sql) {
    int lineNumRef = 1;
    PreparedStatement preparedStatement = getPreparedStatement(sql);

    try (BufferedReader bufferedReader = new BufferedReader(
        getFileReaderFromClassPathResource(resourceName))) {
      report(resourceName + " import process initiated...");
      int numOfLinesToSkip = localSetInfo.getImportProgress(resourceName);
      advanceBufferedReaderToNLine(bufferedReader, numOfLinesToSkip);

      String line = bufferedReader.readLine();
      while (line != null && lineNumRef < (sampleSizeLimit + 1)) {
        getAttributesAndSetPrepStmnt(line, lineNumRef, numOfAttributesExpected, preparedStatement);

        if (lineNumRef % batchSizeUpdateTrigger == 0) {
          preparedStatement.executeBatch();
          commitLocalSetInfoImportProgress();
        }

        localSetInfo.incrementImported(resourceName);
        lineNumRef++;
        line = bufferedReader.readLine();
      }
      preparedStatement.executeBatch();
      commitLocalSetInfoImportProgress();
    } catch (Exception e) {
      report("Error importing dataset records: " + e.getMessage());
    }
  }

  //!===========================================================>
  //
  //? IMPORT DATASET RECORDS HELPERS
  //
  //!===========================================================>

  /**
   * Advances the BufferedReader to the specified line number.
   * 
   * @param bufferedReader the BufferedReader to advance
   * @param n              the line number to advance to
   */
  public void advanceBufferedReaderToNLine(BufferedReader bufferedReader, int n) {
    try {
      for (int i = 0; i < n; i++) {
        bufferedReader.readLine();
      }
    } catch (Exception e) {
      report("Error advancing BufferedReader to line " + n + ": " + e.getMessage());
    }
  }

  /**
   * Commits the import progress of the local set info.
   */
  @SuppressWarnings("null")
  public void commitLocalSetInfoImportProgress() {
    try {
      jdbcTemplate.update(localSetInfo.getSQLUpdateQuery());
    } catch (Exception e) {
      report("Error committing local set info import progress: " + e.getMessage());
    }
  }

  /**
   * Creates a prepared statement for the given SQL String.
   * 
   * @param sql the SQL statement
   * @return the prepared statement
   */
  private PreparedStatement getPreparedStatement(String sql) {
    try {
      return dataSource.getConnection().prepareStatement(sql);
    } catch (SQLException e) {
      report("getPreparedStatement() error: " + e.getMessage());
      return null;
    }
  }

  /**
   * Retrieves a FileReader for the given classpath resource.
   * 
   * @param resourceName the name of the resource file
   * @return the FileReader
   */
  private FileReader getFileReaderFromClassPathResource(String resourceName) {
    try {
      return new FileReader(new ClassPathResource("data/" + resourceName).getFile());
    } catch (Exception e) {
      report("getFileReaderFromClassPathResource() error: " + e.getMessage());
      return null;
    }
  }

  /**
   * Retrieves an array of string attributes from a CSV line.
   * 
   * @param line                   the String of text from a single line of a CSV file
   * @param numOfAttributesExpected the number of expected attributes
   * @return the array of string attributes
   */
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

  /**
   * Retrieves the attributes from a CSV line and sets them in the prepared statement.
   * 
   * @param line                        the String of text from a single line of a CSV file\
   * @param lineNumRef                     the reference number of the line
   * @param numOfAttributesExpected     the number of expected attributes
   * @param preparedStatement          the prepared statement
   */
  private void getAttributesAndSetPrepStmnt(String line, int lineNumRef, int numOfAttributesExpected,
      PreparedStatement preparedStatement) {
    String[] entData = getArrayOfStringAttributesFromCSV(line, numOfAttributesExpected);
    for (int i = 0; i < entData.length; i++) {
      try {
        preparedStatement.setString(i + 1, entData[i]);
      } catch (SQLException e) {
        report("Error setting attribute value of prepared statement: " + e.getMessage());
      }
    }
    try {
      preparedStatement.setInt(entData.length + 1, lineNumRef);
    } catch (SQLException e) {
      report("Error setting line reference value of prepared statement: " + e.getMessage());
    }
    try {
      preparedStatement.addBatch();
    } catch (SQLException e) {
      report("Error adding batch to prepared statement: " + e.getMessage());
    }
  }

  //!===========================================================>
  //
  //? FIND OR RUN .SQL MIGRATION SETS UP SCHEMA (TABLES) IF NEEDED
  //
  //!===========================================================>

  /**
   * Finds or creates the local set schema by querying the database.
   */
  private void findOrCreateLocalSetSchema() {
    try {
      SqlRowSet localSetInfoResults = jdbcTemplate.queryForRowSet("SELECT * FROM local_set_info");
      if (localSetInfoResults.next()) {
        report("Found existing local set info, continuing import process...");
        localSetInfo.mapRowResultsToLocalSetInfo(localSetInfoResults);
      }
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
      executeSQL("LocalSetSchema.sql", jdbcTemplate);
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
