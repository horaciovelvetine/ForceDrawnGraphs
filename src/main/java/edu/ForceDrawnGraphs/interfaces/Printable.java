package edu.ForceDrawnGraphs.interfaces;

/**
 * Provides the default methods for displaying UI (in the console)..
 */

public interface Printable {
  /**
   * Prints the specified message to the console.
   *
   * @param msg the message to be printed
   */
  public default void print(String msg) {
    System.out.println(msg);
  }

  /**
   * Prints the specified exception to the console, calling the exception's getMessage() method.
   *
   * @param e the exception to be printed
   */
  public default void print(Exception e) {
    print(e.getMessage());
  }
}
