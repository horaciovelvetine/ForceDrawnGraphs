package edu.ForceDrawnGraphs;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent

public class TestCommand {

  @ShellMethod("Test command")
  public String test() {
    return "Test command";
  }
}
