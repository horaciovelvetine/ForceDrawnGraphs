package edu.ForceDrawnGraphs.Wikiverse.commands;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Command
@ShellComponent
public class InitWikiset {
  @ShellMethod(key = "init-wikiset", value = "Initialize the Wikiverse dataset application")
  public void initWikiset() {
    throw new UnsupportedOperationException("Not implemented yet.");
  }
}
