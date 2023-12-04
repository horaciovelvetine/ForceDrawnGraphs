package edu.ForceDrawnGraphs.Wikiverse.exceptions;

public class LocalDatabaseConnectionException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LocalDatabaseConnectionException(String getMessageText) {
    super(getMessageText);
  }
}
