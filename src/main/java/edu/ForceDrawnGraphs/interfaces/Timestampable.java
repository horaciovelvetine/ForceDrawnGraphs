package edu.ForceDrawnGraphs.interfaces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface Timestampable {

  /**
   * Returns the current timestamp in the format "yyyy-MM-dd HH:mm:ss".
   *
   * @return the current timestamp
   */
  public default String now() {
    LocalDateTime now = LocalDateTime.now();
    return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}