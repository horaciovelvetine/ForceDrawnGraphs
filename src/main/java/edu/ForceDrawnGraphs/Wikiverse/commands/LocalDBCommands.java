package edu.ForceDrawnGraphs.Wikiverse.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.Wikiverse.db.LocalDatabase;
import edu.ForceDrawnGraphs.Wikiverse.models.Wikiset;

@Command
@ShellComponent
public class LocalDBCommands {
  @Autowired
  private LocalDatabase db;

  @ShellMethod(key = "db-status", value = "Summary of the status of the connection to the local database")
  public void dbStatus() {
    db.checkConnectionIsValid();
    Wikiset ws = db.findOrCreateWikiset();
    ws.countRecordTotals();
  }

}
