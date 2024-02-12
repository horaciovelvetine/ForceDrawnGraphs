package edu.ForceDrawnGraphs.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public interface FindTotalRecordsInFile {
  public default int findTotalRecordsInFile(String filePath) {
    long totalRecords = 0;
    try (InputStream inputStream = getClass().getResourceAsStream(filePath);
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
      while (scanner.hasNextLine()) {
        scanner.nextLine();
        totalRecords++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return (int) totalRecords;
  };
}
