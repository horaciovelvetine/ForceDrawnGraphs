package edu.ForceDrawnGraphs.Wikiverse.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent

public class TestCommand {

  @ShellMethod(key = "test", value = "Test Command")
  public String test() {
    return "Test Command";
  }
}
