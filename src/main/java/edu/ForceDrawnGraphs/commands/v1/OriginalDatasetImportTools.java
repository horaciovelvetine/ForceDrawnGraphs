package edu.ForceDrawnGraphs.commands.v1;

import java.io.BufferedReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ForceDrawnGraphs.functions.AddBatchToStmt;
import edu.ForceDrawnGraphs.functions.ExecuteSQLResourceFile;
import edu.ForceDrawnGraphs.functions.GetBufferedReaderForResource;
import edu.ForceDrawnGraphs.functions.GetPreparedStmt;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.v1.Hyperlink;
import edu.ForceDrawnGraphs.models.v1.LinkAnnotatedTextRecord;
import edu.ForceDrawnGraphs.models.v1.OriginalDatasetImportInfoStruction;
import edu.ForceDrawnGraphs.models.v1.SectionRecord;

@ShellComponent
public class OriginalDatasetImportTools
    implements ExecuteSQLResourceFile, GetPreparedStmt, GetBufferedReaderForResource, AddBatchToStmt {
  private DataSource dataSource;
  private int insertBatchSize = 100000; // How many statements will be added to the batch before it is executed
  private Map<Target, OriginalDatasetImportInfoStruction> originalDatasetImportInfoStructions = new EnumMap<>(
      Target.class);

  public OriginalDatasetImportTools(DataSource dataSource) {
    this.dataSource = dataSource;
    initializeImportInfo();
  }

  @ShellMethod("Write data from dataset files to the PG database (items, pages, properties, statements, links, or all).")
  public void importData(@ShellOption(defaultValue = "no-input") String target) {
    ProcessTimer processTimer = new ProcessTimer("importData(" + target + ")");

    ExecutorService executor = Executors.newCachedThreadPool();
    CompletableFuture<Void> itemFuture = null;
    CompletableFuture<Void> pageFuture = null;
    CompletableFuture<Void> propertyFuture = null;
    CompletableFuture<Void> statementFuture = null;
    CompletableFuture<Void> hyperlinkFuture = null;

    Target importTarget = Target.fromString(target);
    if (importTarget == null) {
      System.out
          .println("Invalid target. Please specify 'items', 'pages', 'properties', 'statements', 'links', or 'all'.");
      return;
    }

    switch (importTarget) {
      case ITEMS:
        itemFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.ITEMS), executor);
        break;
      case PAGES:
        pageFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.PAGES), executor);
        break;
      case PROPERTIES:
        propertyFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.PROPERTIES), executor);
        break;
      case STATEMENTS:
        statementFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.STATEMENTS), executor);
        break;
      case LINKS:
        hyperlinkFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.LINKS), executor);
        break;
      case ALL:
        itemFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.ITEMS), executor);
        pageFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.PAGES), executor);
        propertyFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.PROPERTIES), executor);
        statementFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.STATEMENTS), executor);
        hyperlinkFuture = CompletableFuture.runAsync(() -> importDataFromResourceFile(Target.LINKS), executor);
        break;
    }

    try {
      CompletableFuture<Void> allFutures = CompletableFuture.allOf(itemFuture, pageFuture, propertyFuture,
          statementFuture, hyperlinkFuture);
      allFutures.join();
    } catch (NullPointerException e) {
      System.out.println("");
    } finally {
      executor.shutdown();
    }
    processTimer.end();
  }

  private void importDataFromResourceFile(Target target) {
    OriginalDatasetImportInfoStruction importInfo = originalDatasetImportInfoStructions.get(target);
    ProcessTimer processTimer = new ProcessTimer(
        "importDataFromCSVResourceFile(" + importInfo.resourceFileName + ")");
    int lineNumRef = 1;
    PreparedStatement preparedStatement = getPreparedStmt(importInfo.sqlInsertStatement, dataSource);

    try (BufferedReader bufferedReader = getBufferedReaderForResource("data/" + importInfo.resourceFileName)) {
      String line = bufferedReader.readLine();

      while (line != null) {
        // Check for .JSONL or not 
        if (importInfo.resourceFileName.endsWith(".csv")) {
          getAttributesAndSetPrepStmnt(line, importInfo.expectedNumberOfAttributes, preparedStatement);
          lineNumRef++;
        } else {
          HashMap<Integer, Hyperlink> hyperlinks = getNewHyperlinksFromJSONLine(line);
          for (Hyperlink hyperlink : hyperlinks.values()) {
            addHyperlinkToBatch(preparedStatement, hyperlink);
            lineNumRef++;
          }
        }
        // Check for batch execution commit
        if (lineNumRef % insertBatchSize == 0) {
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

  private enum Target {
    ITEMS,
    PAGES,
    PROPERTIES,
    STATEMENTS,
    LINKS,
    ALL;

    public static Target fromString(String target) {
      try {
        return Target.valueOf(target.toUpperCase());
      } catch (IllegalArgumentException e) {
        return null;
      }
    }
  }

  private void initializeImportInfo() {
    originalDatasetImportInfoStructions.put(Target.ITEMS, new OriginalDatasetImportInfoStruction("item.csv", 3,
        "INSERT INTO items (item_id, en_label, en_description) VALUES (?, ?, ?)"));
    originalDatasetImportInfoStructions.put(Target.PAGES, new OriginalDatasetImportInfoStruction("page.csv", 4,
        "INSERT INTO pages (page_id, item_id, title, views) VALUES (?, ?, ?, ?)"));
    originalDatasetImportInfoStructions.put(Target.LINKS,
        new OriginalDatasetImportInfoStruction("link_annotated_text.jsonl", 0,
            "INSERT INTO hyperlinks (from_page_id, to_page_id, count) VALUES (?, ?, ?)"));
    originalDatasetImportInfoStructions.put(Target.PROPERTIES, new OriginalDatasetImportInfoStruction("property.csv", 3,
        "INSERT INTO properties (property_id, en_label, en_description) VALUES (?, ?, ?)"));
    originalDatasetImportInfoStructions.put(Target.STATEMENTS,
        new OriginalDatasetImportInfoStruction("statements.csv", 3,
            "INSERT INTO statements (source_item_id, edge_property_id, target_item_id) VALUES (?, ?, ?)"));
  }
}
