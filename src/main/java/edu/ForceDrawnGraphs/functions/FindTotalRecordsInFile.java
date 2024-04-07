package edu.ForceDrawnGraphs.functions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;

import edu.ForceDrawnGraphs.interfaces.ProcessTimer;

public interface FindTotalRecordsInFile {

  /**
   * Finds the total number of records in a file.
   * 
   * @param filePath the path of the file to be processed
   * @return the total number of records in the file
   */
  public default int findTotalRecordsInFile(String filePath) {
    ProcessTimer processTimer = new ProcessTimer("FindTotalRecordsInFile(@" + filePath + ") ");
    long totalRecords = 0;
    try (InputStream inputStream = new ClassPathResource(filePath).getInputStream();
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
      while (scanner.hasNextLine()) {
        scanner.nextLine();
        totalRecords++;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("========================\n" + "Error reading lines from file: " + e.getMessage());

    }
    processTimer.end();
    return (int) totalRecords;
  };
}
