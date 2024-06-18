package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.util.ProcessTimer;
import edu.ForceDrawnGraphs.wikidata.services.APIBroker;
import edu.ForceDrawnGraphs.wikidata.services.EntDocProc;

@ShellComponent
public class InitGraphset implements Reportable {
  private static Graphset graphset = new Graphset();
  private static EntDocProc docProc = new EntDocProc(graphset);
  private static APIBroker wikidataAPI = new APIBroker(docProc, graphset);
  private static Integer targetDepth = 3;

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("InitGraphset()::");

    graphset.setOriginQuery(target);
    wikidataAPI.fuzzyFetchOriginEntityDocument(target);

    while (graphset.depth() <= targetDepth) {
      wikidataAPI.fetchQueuedValuesDetails();

      if (!graphset.wikiDataFetchQueue().hasItems(graphset.depth())) {
        graphset.iterateNDepth();
      }

      //TODO - Properties have a label & description and are likely to have a matching Vertex
      // which (using the label) could be preemptively added to QUEUE
      report(graphset.toString());
      timer.lap();
    }
    //TODO - end of the fetch (sh)could close the Vertex data (by fetching remaining ItemDocs)
    timer.end();
    print("stop");

    //! - Start the graph/layout
  }
}
