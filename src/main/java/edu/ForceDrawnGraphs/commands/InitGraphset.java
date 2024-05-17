package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import edu.ForceDrawnGraphs.functions.FindWMEntDocumentFromTitleQuery;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.WikiDocAPIBroker;
import edu.ForceDrawnGraphs.models.WikiDocProcessor;

@ShellComponent
public class InitGraphset implements FindWMEntDocumentFromTitleQuery {
  private static Graphset graphset = new Graphset();
  private static WikiDocProcessor docProc = new WikiDocProcessor(graphset);
  private static WikiDocAPIBroker brokerService = new WikiDocAPIBroker(docProc);

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("initGraphset(" + target + ") in InitGraphset.java");
    brokerService.fuzzyFetchOriginEntityDocument(target); // fetch origin entity document, and init graphset data management

    while (graphset.getFetchQueue().isEmpty() == false) {
      // should iterate over the fetchQueue, fetching each QID and adding it to the graphset
    }

    timer.end();
  }
}
