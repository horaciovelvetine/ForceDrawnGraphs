package edu.ForceDrawnGraphs.Wikiverse.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.Wikiverse.db.LocalDatabase;

@Command
@ShellComponent
public class WikisetCommands {
  @Autowired
  private LocalDatabase db;

  @ShellMethod(key = "ws-create", value = "Init a local representation of the Wikiset to the PG database")
  public void wsCreate() {
    db.createWikiset();
  }

  @ShellMethod(key = "ws-digest", value = "Begin or resume ingesting the Wikiset into the PG database")
  public void wsDigest() {
    db.digestWikiset();
  }
}
