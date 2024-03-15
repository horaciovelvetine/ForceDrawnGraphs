package edu.ForceDrawnGraphs.commands;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.sql.DataSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ForceDrawnGraphs.models.Hyperlink;
import edu.ForceDrawnGraphs.models.LinkAnnotatedTextRecord;
import edu.ForceDrawnGraphs.models.LocalSetInfo;
import edu.ForceDrawnGraphs.models.SectionRecord;
import edu.ForceDrawnGraphs.util.ExecuteSQL;
import edu.ForceDrawnGraphs.util.ProcessTimer;
import edu.ForceDrawnGraphs.util.Reportable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

@ShellComponent
public class BuildLocalSet implements ExecuteSQL, Reportable {
  private DataSource dataSource;
  private JdbcTemplate jdbcTemplate;
  private LocalSetInfo localSetInfo = new LocalSetInfo();
  private int batchSizeUpdateTrigger = 100000; // How often to commit batches to the database

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
    ProcessTimer processTimer = new ProcessTimer("build(batchSize = " + batchSizeUpdateTrigger + ") run 1 -->");
    ExecutorService executor = Executors.newCachedThreadPool();

    CompletableFuture<Void> itemFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("item.csv", 3,
          "INSERT INTO items (item_id, en_label, en_description, line_ref) VALUES (?, ?, ?, ?)");
    }, executor);

    CompletableFuture<Void> pageFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("page.csv", 4,
          "INSERT INTO pages (page_id, item_id, title, views, line_ref) VALUES (?, ?, ?, ?, ?)");
    }, executor);

    CompletableFuture<Void> propertyFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("property.csv", 3,
          "INSERT INTO properties (property_id, en_label, en_description, line_ref) VALUES (?, ?, ?, ?)");
    }, executor);

    CompletableFuture<Void> statementsFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("statements.csv", 3,
          "INSERT INTO statements (source_item_id, edge_property_id, target_item_id, line_ref) VALUES (?, ?, ?, ?)");
    }, executor);

    CompletableFuture<Void> linkAnnotatedTextFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("link_annotated_text.jsonl", 0,
          "INSERT INTO hyperlinks (from_page_id, to_page_id, count, line_ref) VALUES (?, ?, ?, ?)");
    }, executor);

    CompletableFuture<Void> allFutures = CompletableFuture.allOf(itemFuture, pageFuture, propertyFuture, statementsFuture,
        linkAnnotatedTextFuture);

    allFutures.join();
    processTimer.end();
    executor.shutdown();
    // importDataFromResourceFile("item.csv", 3,
    //     "INSERT INTO items (item_id, en_label, en_description, line_ref) VALUES (?, ?, ?, ?)");
    // processTimer.lap();
    // importDataFromResourceFile("page.csv", 4,
    //     "INSERT INTO pages (page_id, item_id, title, views, line_ref) VALUES (?, ?, ?, ?, ?)");
    // processTimer.lap();
    // importDataFromResourceFile("property.csv", 3,
    //     "INSERT INTO properties (property_id, en_label, en_description, line_ref) VALUES (?, ?, ?, ?)");
    // processTimer.lap();
    // importDataFromResourceFile("statements.csv", 3,
    //     "INSERT INTO statements (source_item_id, edge_property_id, target_item_id, line_ref) VALUES (?, ?, ?, ?)");
    // processTimer.lap();
    // importDataFromResourceFile("link_annotated_text.jsonl", 0,
    //     "INSERT INTO hyperlinks (from_page_id, to_page_id, count, line_ref) VALUES (?, ?, ?, ?)");
    // processTimer.lap();
  }

  //!===========================================================>
  //? IMPORT DATASET RECORDS HELPERS
  //!===========================================================>

  /** BuildLocalSet.java
   * Imports dataset records from a file into the database.
   * @param resourceName            the name of the resource file
   * @param numOfAttributesExpected the number of expected attributes in each record
   * @param sql                     the SQL statement for inserting records
   * 
   * @implNote This method requires the line_ref to be the last attribute in the insert SQL statement.
   */
  private void importDataFromResourceFile(String resourceName, int numOfAttributesExpected, String sql) {
    ProcessTimer processTimer = new ProcessTimer(
        "importDataFromCSVResourceFile(" + resourceName + ")");
    int lineNumRef = localSetInfo.getImportProgress(resourceName);
    PreparedStatement preparedStatement = getPreparedStatement(sql);

    try (BufferedReader bufferedReader = new BufferedReader(getFileReaderFromResource(resourceName))) {

      advanceReaderToLineNumRef(bufferedReader, lineNumRef);
      String line = bufferedReader.readLine();

      while (line != null) {
        // Check for .JSONL or not 
        if (resourceName.endsWith(".csv")) {
          getAttributesAndSetPrepStmnt(line, lineNumRef, numOfAttributesExpected, preparedStatement);
        } else {
          HashMap<Integer, Hyperlink> hyperlinks = getNewHyperlinksFromJSONLine(line, lineNumRef);
          for (Hyperlink hyperlink : hyperlinks.values()) {
            addHyperlinkToBatch(preparedStatement, hyperlink);
          }
        }
        // Check for batch execution commit
        if (lineNumRef % batchSizeUpdateTrigger == 0) {
          preparedStatement.executeBatch();
          commitLocalSetInfoImportProgress();
          processTimer.lap();
        }
        //increment and move on to next line
        localSetInfo.incrementImported(resourceName);
        lineNumRef++;
        line = bufferedReader.readLine();
      }
      // commit remainder batch ...
      preparedStatement.executeBatch();
      commitLocalSetInfoImportProgress();
      // ends...
    } catch (Exception e) {
      report("Error importing dataset records: " + e.getMessage());
    } finally {
      processTimer.end();
    }
  }

  /**
   * Finds if any progress has already been made importing this file, then advances the reader to that line.
   * 
   * @param bufferedReader the BufferedReader to advance
   * @param n              the lineNumRef (deafult of 1) to advance to
   */
  public void advanceReaderToLineNumRef(BufferedReader bufferedReader, int n) {
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
  private FileReader getFileReaderFromResource(String resourceName) {
    try {
      return new FileReader(new ClassPathResource("data/" + resourceName).getFile());
    } catch (Exception e) {
      report("getFileReaderFromResource() error: " + e.getMessage());
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
    setLineRefOnPrepStmnt(preparedStatement, lineNumRef, entData);
    addBatchToPrepSmnt(preparedStatement);
  }

  /**
   * Sets the line reference value on the prepared statement.
   *
   * @param preparedStatement The prepared statement to set the line reference value on.
   * @param lineNumRef The line number reference value to set.
   * @param entData The entity data array. Used to set the line reference value at the end of the array.
   */
  private void setLineRefOnPrepStmnt(PreparedStatement preparedStatement, int lineNumRef, String[] entData) {
    try {
      preparedStatement.setInt(entData.length + 1, lineNumRef);
    } catch (SQLException e) {
      report("Error setting line reference value of prepared statement: " + e.getMessage());
    }
  }

  /**
   * Adds the current batch to the prepared statement.
   *
   * @param preparedStatement the prepared statement to add the batch to
   */

  private void addBatchToPrepSmnt(PreparedStatement preparedStatement) {
    try {
      preparedStatement.addBatch();
    } catch (SQLException e) {
      report("Error adding batch to prepared statement: " + e.getMessage());
    }
  }

  private HashMap<Integer, Hyperlink> getNewHyperlinksFromJSONLine(String JSONLineObj, int lineNumRef) {
    HashMap<Integer, Hyperlink> hyperlinks = new HashMap<>();
    LinkAnnotatedTextRecord record = serialzeLinkAnnotatedTextObject(JSONLineObj);
    for (SectionRecord sectionRecord : record.sections) {
      for (int targetPageId : sectionRecord.targetPageIds) {
        if (hyperlinks.containsKey(targetPageId)) {
          hyperlinks.get(targetPageId).incrementCount();
        } else {
          hyperlinks.put(targetPageId, new Hyperlink(record.pageId, targetPageId, lineNumRef));
        }
      }
    }
    return hyperlinks;
  }

  private LinkAnnotatedTextRecord serialzeLinkAnnotatedTextObject(String JSONLineObj) {
    ObjectMapper mapper = new ObjectMapper();
    LinkAnnotatedTextRecord record = null;
    try {
      record = mapper.readValue(JSONLineObj, LinkAnnotatedTextRecord.class);
    } catch (Exception e) {
      report("Error serializing JSON line object: " + e.getMessage());
    }
    return record;
  }

  private void addHyperlinkToBatch(PreparedStatement preparedStatement, Hyperlink hyperlink) {
    try {
      preparedStatement.setInt(1, hyperlink.getFrom_page_id());
      preparedStatement.setInt(2, hyperlink.getTo_page_id());
      preparedStatement.setInt(3, hyperlink.getCount());
      preparedStatement.setInt(4, hyperlink.getLineRef());
      preparedStatement.addBatch();
    } catch (SQLException e) {
      report("Error adding hyperlink to batch: " + e.getMessage());
    }
  }

}
