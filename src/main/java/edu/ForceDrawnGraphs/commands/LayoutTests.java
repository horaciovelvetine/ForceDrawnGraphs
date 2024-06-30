package edu.ForceDrawnGraphs.commands;

import java.util.concurrent.CompletableFuture;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.jung.GraphsetDecorator;
import edu.ForceDrawnGraphs.util.FirstResponder;
import edu.ForceDrawnGraphs.models.Graphset;

@ShellComponent
public class LayoutTests implements Reportable {
  private static final Graphset testset = FirstResponder.desrializeResponset();
  private static final GraphsetDecorator graphsetDec = new GraphsetDecorator(testset);

  @ShellMethod("Run layout tests.")
  public void layout() {

    //JUNG LAYOUTS
    CompletableFuture<Void> initFRFuture = CompletableFuture.runAsync(() -> graphsetDec.initFR());
    CompletableFuture<Void> initFR2Future = CompletableFuture.runAsync(() -> graphsetDec.initFR2());

    CompletableFuture<Void> allFutures =
        CompletableFuture.allOf(initFRFuture, initFR2Future);
    allFutures.join();
    print("Running layout tests...");
  }
}
