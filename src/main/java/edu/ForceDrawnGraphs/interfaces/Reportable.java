package edu.ForceDrawnGraphs.interfaces;

/**
 * A utility interface for reporting (printing and logging), runtime details.
 */
public interface Reportable extends Printable, Loggable {

  /**
   * Print and log a string msg.
   *
   * @param msg the message to be reported
   */
  public default void report(String msg) {
    print(msg);
    log(msg);
  }

  /**
   * Print and log an exception.
   *
   * @param e the exception to be reported
   */
  public default void report(Exception e) {
    print(e);
    log(e);
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
