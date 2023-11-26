package edu.ForceDrawnGraphs.Wikiverse.commands;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Command
@ShellComponent
public class TestCommand {

  @ShellMethod(key = "hello", value = "Prints hello world")
  public String hello() {
    return "Hello world!";
  }
}
