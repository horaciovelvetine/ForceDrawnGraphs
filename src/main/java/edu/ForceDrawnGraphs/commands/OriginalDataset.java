package edu.ForceDrawnGraphs.commands;

import java.io.BufferedReader;

import javax.sql.DataSource;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ForceDrawnGraphs.functions.AddBatchToStmt;
import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;
import edu.ForceDrawnGraphs.functions.GetBufferedReaderForResource;
import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.Hyperlink;
import edu.ForceDrawnGraphs.models.LinkAnnotatedTextRecord;
import edu.ForceDrawnGraphs.models.SectionRecord;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CompletableFuture;

@ShellComponent
public class OriginalDataset
    implements ExecuteSQLResourceFile, GetPreparedStmt, GetBufferedReaderForResource, AddBatchToStmt {
  private DataSource dataSource;
  private int batchSizeUpdateTrigger = 100000; // How often to commit batches to the database

  /**
   * Constructor for BuildLocalSet.
   * 
   * @param dataSource configured in application.properties for the database
   */
  public OriginalDataset(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
  * Builds, or resumes building, the local set.
  */
  @ShellMethod("Builds, or resumes building, the local set.")
  public void build() {
    ProcessTimer processTimer = new ProcessTimer("build(batchSize = " + batchSizeUpdateTrigger + ")");
    ExecutorService executor = Executors.newCachedThreadPool();

    CompletableFuture<Void> itemFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("item.csv", 3,
          "INSERT INTO items (item_id, en_label, en_description) VALUES (?, ?, ?)");
    }, executor);

    CompletableFuture<Void> pageFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("page.csv", 4,
          "INSERT INTO pages (page_id, item_id, title, views) VALUES (?, ?, ?, ?)");
    }, executor);

    CompletableFuture<Void> propertyFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("property.csv", 3,
          "INSERT INTO properties (property_id, en_label, en_description) VALUES (?, ?, ?)");
    }, executor);

    CompletableFuture<Void> statementsFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("statements.csv", 3,
          "INSERT INTO statements (source_item_id, edge_property_id, target_item_id) VALUES (?, ?, ?)");
    }, executor);

    CompletableFuture<Void> linkAnnotatedTextFuture = CompletableFuture.runAsync(() -> {
      importDataFromResourceFile("link_annotated_text.jsonl", 0,
          "INSERT INTO hyperlinks (from_page_id, to_page_id, count) VALUES (?, ?, ?)");
    }, executor);

    CompletableFuture<Void> allFutures = CompletableFuture.allOf(itemFuture, pageFuture, propertyFuture,
        statementsFuture,
        linkAnnotatedTextFuture);

    allFutures.join();
    processTimer.end();
    executor.shutdown();
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
    int lineNumRef = 1;
    PreparedStatement preparedStatement = getPreparedStmt(sql, dataSource);

    try (BufferedReader bufferedReader = getBufferedReaderForResource("data/" + resourceName)) {
      String line = bufferedReader.readLine();

      while (line != null) {
        // Check for .JSONL or not 
        if (resourceName.endsWith(".csv")) {
          getAttributesAndSetPrepStmnt(line, numOfAttributesExpected, preparedStatement);
          lineNumRef++;
        } else {
          HashMap<Integer, Hyperlink> hyperlinks = getNewHyperlinksFromJSONLine(line);
          for (Hyperlink hyperlink : hyperlinks.values()) {
            addHyperlinkToBatch(preparedStatement, hyperlink);
            lineNumRef++;
          }
        }
        // Check for batch execution commit
        if (lineNumRef % batchSizeUpdateTrigger == 0) {
          preparedStatement.executeBatch();
          processTimer.lap();
        }
        //increment and move on to next line
        line = bufferedReader.readLine();
      }
      // commit remainder batch ...
      preparedStatement.executeBatch();
      // ends...
    } catch (Exception e) {
      report("Error importing dataset records: " + e.getMessage());
    } finally {
      processTimer.end();
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
    // setLineRefOnPrepStmnt(preparedStatement, lineNumRef, entData);
    addBatchToStmt(preparedStatement);
  }

  private HashMap<Integer, Hyperlink> getNewHyperlinksFromJSONLine(String JSONLineObj) {
    HashMap<Integer, Hyperlink> hyperlinks = new HashMap<>();
    LinkAnnotatedTextRecord record = serialzeLinkAnnotatedTextObject(JSONLineObj);
    for (SectionRecord sectionRecord : record.sections) {
      for (int targetPageId : sectionRecord.targetPageIds) {
        if (hyperlinks.containsKey(targetPageId)) {
          hyperlinks.get(targetPageId).incrementCount();
        } else {
          String targetPageIDString = Integer.toString(targetPageId);
          String sourcePageIDString = Integer.toString(record.pageId);
          hyperlinks.put(targetPageId, new Hyperlink(sourcePageIDString, targetPageIDString));
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
      preparedStatement.setString(1, hyperlink.getFromPageID());
      preparedStatement.setString(2, hyperlink.getToPageID());
      preparedStatement.setString(3, hyperlink.getCount());
      preparedStatement.addBatch();
    } catch (SQLException e) {
      report("Error adding hyperlink to batch: " + e.getMessage());
    }
  }

}