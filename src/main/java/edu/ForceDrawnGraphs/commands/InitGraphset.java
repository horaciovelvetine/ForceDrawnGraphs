package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.wikidata.services.APIBroker;
import edu.ForceDrawnGraphs.models.wikidata.services.EntDocProc;
import edu.ForceDrawnGraphs.util.ProcessTimer;

@ShellComponent
public class InitGraphset implements Reportable {
  private static Graphset graphset = new Graphset();
  private static EntDocProc docProc = new EntDocProc(graphset);
  private static APIBroker brokerService = new APIBroker(docProc);
  private int fetchDepth = 2;

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("initGraphset(" + target + ") in InitGraphset.java");
    brokerService.fuzzyFetchOriginEntityDocument(target); // fetch origin entity document, and init graphset data management

    while (graphset.fetchQueue().hasItems() && fetchDepth-- > 0) {
      // should iterate over the fetchQueue, fetching until nothing remains
      brokerService.fetchItemsInQueue(graphset);
    }
    timer.end();
    print("Graphset initialized.");
  }
}
