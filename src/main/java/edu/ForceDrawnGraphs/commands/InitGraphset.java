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

      wikidataAPI.fetchQueuedValuesDetails(graphset.depth());

      if (!graphset.wikiDataFetchQueue().hasItems(graphset.depth())) {
        graphset.iterateNDepth();
      }

      // Q/P Ents which have the same label and are meant to represent the same thing "P1963" properties for this type "when this subject is used as object of "instance of" the following props normally apply
      // rn properties in the entityQueue happen because they are the tgtValue given - this may be a handy check to add an extra search value (the label of this result) to the query queue (to find its inevitable Vertex twin)

      report("\n" + "GRAPHSET//GRAPHSET//GRAPHSET//GRAPHSET//" + "\n", graphset.toString());
      timer.lap();
    }
    // at the end here, should do a stunted version of the fetch for all the item ("Q123" elements w/o checking edges, or just putting there edges in to fetch or something to have a vertext complete graphset)
    timer.end();
  }
}
