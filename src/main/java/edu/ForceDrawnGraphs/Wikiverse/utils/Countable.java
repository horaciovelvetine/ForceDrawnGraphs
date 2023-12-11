package edu.ForceDrawnGraphs.Wikiverse.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public interface Countable {
  public default long countLines(String filePath) {
    long totalRecords = 0;
    try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
      totalRecords = lines.count();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return totalRecords;
  }
}
