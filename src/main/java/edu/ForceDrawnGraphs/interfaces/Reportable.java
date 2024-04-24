package edu.ForceDrawnGraphs.interfaces;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An interface for logging and reporting messages and exceptions during runtime, in a meaningful way.
 */
public interface Reportable {

  /**
   * Default Log file path.
   */
  public static final String LOG_FILE_PATH = "src/main/resources/logs/";

  /**
   * Returns the current timestamp in the format "yyyy-MM-dd HH:mm:ss".
   *
   * @return the current timestamp
   */
  public default String getTimeStamp() {
    LocalDateTime now = LocalDateTime.now();
    return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  /**
   * Timestamps & prints exception to the default log file, by calling .getMessage() on the exception.
   *
   * @param e the exception to be logged
   */
  public default void log(Exception e) {
    log(e.getMessage());
  }

  /**
   * Timestamps & prints exception to the default log file.
   *
   * @param message the message to be logged
   */
  public default void log(String message) {
    log(message, "debug.log");
  }

  /**
   * Timestamps & prints the specified message to a specified log file (file location printed to the console).
   *
   * @param message     the message to be logged
   * @param logFileName the name of the log file
   */
  public default void log(String message, String logFileName) {
    print("Logged@ " + LOG_FILE_PATH + logFileName);
    try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH + logFileName, true))) {
      writer.println("\n" + getTimeStamp() + "\n" + message);
    } catch (IOException e) {
      print("\n" + e.getMessage() + "\n" + "Error in logging process: " + "\n" + message);
    }
  }

  /**
   * Prints message to the console.
   *
   * @param msg the message to be printed
   */
  public default void print(String msg) {
    System.out.println(msg + "\n");
  }

  /**
   * Prints exception to the console.
   *
   * @param e the exception to be printed
   */
  public default void print(Exception e) {
    print(e.getMessage());
  }

  /**
   * Reports message by printing it to the console and logging it.
   *
   * @param msg the message to be reported
   */
  public default void report(String msg) {
    print(msg);
    log(msg);
  }

  /**
   * Reports exception by printing it to the console and logging it.
   *
   * @param e the exception to be reported
   */
  public default void report(Exception e) {
    print(e);
    log(e);
  }

  /**
   * Reports message by printing it to the console and logging it to the specified log file.
   *
   * @param msg         the message to be reported
   * @param logFileName the name of the log file
   */
  public default void report(String msg, String logFileName) {
    print(msg);
    log(msg, logFileName);
  }

  /**
   * Reports message by printing it to the console and logging it to the default log file.
   *
   * @param originMsg   Message placed before the exception message to include more context
   * @param e           the exception to be reported
   */
  public default void report(String originMsg, Exception e) {
    String msg = originMsg + "\n" + e.getMessage();
    report(msg);
  }

}
