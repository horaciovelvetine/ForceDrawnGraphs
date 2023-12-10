package edu.ForceDrawnGraphs.Wikiverse.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface Loggable {
  public static final String LOG_FILE_PATH = "src/main/resources/logs/";

  public default void log(Exception e) {
    log(e.getMessage());
  }

  public default void log(String message) {
    log(message, "debug.log");
  }

  public default void log(String message, String logFileName) {
    print("Error logged to: " + LOG_FILE_PATH + logFileName);
    try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH + logFileName, true))) {
      writer.println(getTimeStamp() + "\n" + message);
    } catch (IOException e) {
      print("\n" + e.getMessage() + "\n" + "Error in logging process: " + "\n" + message);
    }
  }

  public default String getTimeStamp() {
    LocalDateTime now = LocalDateTime.now();
    return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public default void print(String msg) {
    System.out.println(msg);
  }

  public default void print(Exception e) {
    print(e.getMessage());
  }

  public default void clearConsole() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public default void clear() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

}
