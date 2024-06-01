package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.wikidata.services.APIBroker;
import edu.ForceDrawnGraphs.models.wikidata.services.EntDocProc;

@ShellComponent
public class InitGraphset implements Reportable {
  private static Graphset graphset = new Graphset();
  private static EntDocProc docProc = new EntDocProc(graphset);
  private static APIBroker brokerService = new APIBroker(docProc);
  private int fetchDepth = 100;

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    graphset.setOriginQuery(target); // set the origin query for the graphset
    brokerService.fuzzyFetchOriginEntityDocument(target); // fetch origin entity document, and init graphset data management

    while (graphset.fetchQueue().hasItems() && fetchDepth-- > 0) {
      // TODO iterate over the fetchQueue, fetching until nothing remains
      // brokerService.fetchItemsInQueue(graphset);
    }
    print("Stop graphset end init.");
  }
}
