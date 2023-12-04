package edu.ForceDrawnGraphs.Wikiverse.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import edu.ForceDrawnGraphs.Wikiverse.db.LocalDatabase;

@Command
@ShellComponent
public class TestCommand {
  @Autowired
  private LocalDatabase db;

  @ShellMethod(key = "hello", value = "Prints hello world")
  public void hello() {
    db.checkConnection();
  }
}
