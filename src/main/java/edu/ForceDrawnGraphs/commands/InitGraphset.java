package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.jung.GraphsetDecorator;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.util.ProcessTimer;
import edu.ForceDrawnGraphs.wikidata.services.APIBroker;
import edu.ForceDrawnGraphs.wikidata.services.EntDocProc;

@ShellComponent
public class InitGraphset implements Reportable {
  // Wikidata
  private static Graphset graphset = new Graphset();
  private static EntDocProc docProc = new EntDocProc(graphset);
  private static APIBroker wikidataAPI = new APIBroker(docProc, graphset);
  //JUNG
  private static GraphsetDecorator graphsetDec = new GraphsetDecorator(graphset);
  // Configuration
  private static Integer targetDepth = 2;

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("InitGraphset()::" + target + "::n=" + targetDepth);

    graphset.setOriginQuery(target);
    wikidataAPI.fuzzyFetchOriginEntityDocument(target);

    // graphsetDec.addGraphEventListener(event -> {
    //   print(event.toString());
    // });

    while (graphset.depth() <= targetDepth) {
      wikidataAPI.fetchQueuedValuesDetails();
      graphsetDec.addCompleteWikidataEnts();

      if (!graphset.wikiDataFetchQueue().hasItems(graphset.depth())) {
        graphset.iterateNDepth();
      }

      timer.lap();
    }
    timer.end();
    //JUNG LAYOUTS
    // CompletableFuture<Void> initFRFuture = CompletableFuture.runAsync(() -> graphsetDec.initFR());
    // CompletableFuture<Void> initFR2Future = CompletableFuture.runAsync(() -> graphsetDec.initFR2());

    // CompletableFuture<Void> allFutures =
    //     CompletableFuture.allOf(initFRFuture, initFR2Future);
    // allFutures.join();

    // FirstResponder.serializeResponset(graphset);
  }
}
