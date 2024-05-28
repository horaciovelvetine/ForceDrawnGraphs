package edu.ForceDrawnGraphs.interfaces;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An interface for writing messages and exceptions to a file, in a meaningful way.
 */
public interface Loggable extends Timestampable {
  public static final String LOG_FILE_PATH = "src/main/resources/logs/";

  public default void log(Exception e) {
    log(e.getMessage());
  }

  public default void log(String message) {
    try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH + "debug.log", true);
        PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.println(now() + ":\n" + message);
    } catch (IOException e) {
      throw new RuntimeException("Error writing to log file: " + e.getMessage());
    }
  }

  public default void log(String headerMsg, Exception e) {
    log(headerMsg + "\n" + e.getMessage());
  }
}
