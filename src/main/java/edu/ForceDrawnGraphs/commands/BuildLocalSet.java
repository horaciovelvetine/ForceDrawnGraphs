package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class BuildLocalSet {

  @ShellMethod("pg-build")
  public String build() {
    return "pg-build";
  }

}
