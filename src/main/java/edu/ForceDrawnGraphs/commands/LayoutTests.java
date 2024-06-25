package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.util.FirstResponder;
import edu.ForceDrawnGraphs.models.Graphset;

@ShellComponent
public class LayoutTests implements Reportable {
  private static final Graphset testset = FirstResponder.desrializeResponset();

  @ShellMethod("Run layout tests.")
  public void layout() {
    print("Running layout tests...");
  }
}
