package edu.ForceDrawnGraphs.commands;

import java.io.BufferedReader;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.functions.AddBatchToStmt;
import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;
import edu.ForceDrawnGraphs.functions.GetBufferedReaderForResource;
import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.functions.GetStringArrayAttributesFromCSVLine;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.Hyperlink;
import edu.ForceDrawnGraphs.models.LinkAnnotatedTextRecord;
import edu.ForceDrawnGraphs.models.SectionRecord;

/**
 * This class imports the original Kensho derived wikimedia dataset to the local PostgreSQL database.
 * 
 * @version 1.0
 * @since 1.0
 */

@ShellComponent
public class OriginalDataset
    implements ExecuteSQLResourceFile, GetPreparedStmt, AddBatchToStmt, GetBufferedReaderForResource,
    GetStringArrayAttributesFromCSVLine {
  private DataSource dataSource;
  Integer batchCounter = 0;
  Integer batchSizeLimit = 100000;

  @SuppressWarnings("null")
  public OriginalDataset(DataSource datasource) {
    this.dataSource = datasource;
  }

  @ShellMethod("Import the original Kensho derived wikimedia dataset to the local PostgreSQL database.")
  public void importDataset() {
    ProcessTimer timer = new ProcessTimer("importDataset(batchSize=" + batchSizeLimit + ") in OriginalDataset.java");
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

    // CompletableFuture<Void> statementsFuture = CompletableFuture.runAsync(() -> {
    //   importDataFromResourceFile("statements.csv", 3,
    //       "INSERT INTO statements (source_item_id, edge_property_id, target_item_id) VALUES (?, ?, ?)");
    // }, executor);

    // CompletableFuture<Void> linkAnnotatedTextFuture = CompletableFuture.runAsync(() -> {
    //   importDataFromResourceFile("link_annotated_text.jsonl", 0,
    //       "INSERT INTO hyperlinks (from_page_id, to_page_id, count) VALUES (?, ?, ?)");
    // }, executor);

    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
       itemFuture, pageFuture, propertyFuture);

    allFutures.join();
    timer.end();
    executor.shutdown();
  }

  /**
   * Imports data from a resource file into the database in a preapred way for later analysis.
   * 
   * @param resourceFileName       The name of the resource file to import.
   * @param numOfExpectedAttributes The number of expected attributes in the resource file.
   * @param insertSQL              The SQL statement to insert the data into the database.
   * @return void
   * 
   */
  private void importDataFromResourceFile(String resourceFileName, int numOfExpectedAttributes, String insertSQL) {
    ProcessTimer timer = new ProcessTimer(
        "importDataFromResourceFile( " + resourceFileName + " ) in OriginalDataset.java");
    PreparedStatement preparedStmt = getPreparedStmt(insertSQL, dataSource);

    try (BufferedReader reader = getBufferedReaderForResource("data/" + resourceFileName);) {
      String line = reader.readLine();
      while (line != null) {
        if (resourceFileName.endsWith(".csv")) {
          getAndSetAttributesForCSVFileOBJ(line, numOfExpectedAttributes, preparedStmt);
        } else {
          getAndSetAttributesForJSONLFileOBJ(line, preparedStmt);
        }
        batchCounter++; // increment the batch counter for set and add
        if (batchCounter % batchSizeLimit == 0) {
          preparedStmt.executeBatch();
          timer.lap();
          batchCounter = 0;
        }
        line = reader.readLine();
      }
      preparedStmt.executeBatch(); // exectue the remainder of any batch
      timer.lap();
    } catch (Exception e) {
      report("importDataFromResourceFile() in OriginalDataset.java was unable to process: " + resourceFileName
          + "see more details in the debug log", e);
    } finally {
      try {
        preparedStmt.close();
      } catch (Exception e) {
        report("importDataFromResourceFile() in OriginalDataset.java was unable to close the prepared statement",
            e);
      }
      timer.end();
    }
  }

  /**
   * Using the stringified line of data from a CSV file, this method parses the data and then sets the parsed attributes on the prepared statement, and adds that OBJ to the batch.
   * 
   * @param line                  The line of data from the CSV file.
   * @param numOfExpectedAttributes The number of expected attributes in the resource file.
   * @param preparedStmt          The prepared statement to set the attributes on.
   * @return void
   * 
   */
  private void getAndSetAttributesForCSVFileOBJ(String line, int numOfExpectedAttributes,
      PreparedStatement preparedStmt) {
    String[] objAttributes = getStringArrayAttributesFromCSVLine(line, numOfExpectedAttributes);
    for (int i = 0; i < objAttributes.length; i++) {
      try {
        preparedStmt.setString(i + 1, objAttributes[i]);
      } catch (Exception e) {
        report("getAndSetAttributesForCSVFileOBJ() in OriginalDataset.java was unable to process: " + line
            + "see more details in the debug log", e);
      }
    }
    addBatchToStmt(preparedStmt);
  }

  /**
   * Using the stringified line of data from a JSONL file, this method parses the data and then sets the parsed attributes on the prepared statement, and adds that OBJ to the batch.
   * 
   * @param line         The line of data from the JSONL file.
   * @param preparedStmt The prepared statement to set the attributes on.
   * @return void
   * 
   */
  private void getAndSetAttributesForJSONLFileOBJ(String line,
      PreparedStatement preparedStmt) {
    HashMap<Integer, Hyperlink> hyperlinks = getHyperlinksFromLine(line);
    for (Hyperlink hyperlink : hyperlinks.values()) {
      hyperlink.setAndAddHyperlinkToBatch(preparedStmt);
    }
  }

  /**
   * Using the stringified line of data from a JSONL file, this method parses the data and then sets the parsed attributes on the prepared statement, and adds that OBJ to the batch.
   * 
   * @param line         The line of data from the JSONL file.
   * @return HashMap<Integer, Hyperlink>
   * 
   */
  private HashMap<Integer, Hyperlink> getHyperlinksFromLine(String line) {
    HashMap<Integer, Hyperlink> hyperlinks = new HashMap<>();
    LinkAnnotatedTextRecord record = new LinkAnnotatedTextRecord().createLATRFromStringData(line);
    for (SectionRecord sectionRecord : record.sections) {
      for (int targetPageID : sectionRecord.targetPageIds) {
        if (hyperlinks.containsKey(targetPageID)) {
          hyperlinks.get(targetPageID).incrementCount();
        } else {
          String fromPageId = Integer.toString(record.pageId);
          Hyperlink hyperlink = new Hyperlink(fromPageId, Integer.toString(targetPageID));
          hyperlinks.put(targetPageID, hyperlink);
        }
      }
    }
    return hyperlinks;
  }
}