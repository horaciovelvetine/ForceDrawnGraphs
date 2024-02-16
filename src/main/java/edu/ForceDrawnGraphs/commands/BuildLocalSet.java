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
  private int preparedStatementUpdateTrigger = 10;

  /**
   * Constructor for BuildLocalSet.
   * 
   * @param dataSource configured in application.properties for the database
   */
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
    importDatasetRecordsFromFile("resourceName", 0, "SQL INSERT STMNT");
    //! END OF BUILD COMMAND EXECUTION
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
    int numOfObjectsInPrepSmnt = 0;
    PreparedStatement preparedStatement = getPreparedStatement(sql);
    try (BufferedReader bufferedReader = new BufferedReader(
        getFileReaderFromClassPathResource(resourceName))) {
      String line = bufferedReader.readLine();
      
      //TODO: ADD CHECK FOR HEADER ROW?
      //TODO: ADD SKIP ROWS FOR INFO THAT IS ALREADY IN THE DB
      //TODO: ADD LINE_REF ATTRIBUTE SETUP FOR EACH RECORD INTO PROCESS

      while (line != null) {
        getAttributesAndSetPrepStmnt(line, numOfAttributesExpected, preparedStatement);

        //TODO: update increment to work off metadata file? 
        localSetInfo.increment("items");
        numOfObjectsInPrepSmnt++;

        if (numOfObjectsInPrepSmnt == preparedStatementUpdateTrigger) {
          preparedStatement.executeUpdate();
          numOfObjectsInPrepSmnt = 0;
        }
        line = bufferedReader.readLine();
      }
      preparedStatement.executeUpdate();
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
   * @param path the path of the resource file
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
   * @param line                        the String of text from a single line of a CSV file
   * @param numOfAttributesExpected     the number of expected attributes
   * @param preparedStatement          the prepared statement
   */
  private void getAttributesAndSetPrepStmnt(String line, int numOfAttributesExpected,
      PreparedStatement preparedStatement) {
    String[] entData = getArrayOfStringAttributesFromCSV(line, numOfAttributesExpected);
    for (int i = 0; i < entData.length; i++) {
      try {
        preparedStatement.setString(i + 1, entData[i]);
      } catch (SQLException e) {
        report("Error setting attribute value of prepared statement: " + e.getMessage());
      }
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
