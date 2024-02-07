package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class HelloWorld {

  @ShellMethod("test")
  public String test() {
    return "Hello World.";
  }
}
