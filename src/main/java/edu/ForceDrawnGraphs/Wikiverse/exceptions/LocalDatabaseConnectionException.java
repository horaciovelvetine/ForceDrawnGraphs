package edu.ForceDrawnGraphs.Wikiverse.exceptions;

public class LocalDatabaseConnectionException extends RuntimeException {

  public LocalDatabaseConnectionException(String getMessageText) {
    super(getMessageText);
  }
}
