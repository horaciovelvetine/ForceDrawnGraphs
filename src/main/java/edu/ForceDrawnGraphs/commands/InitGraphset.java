package edu.ForceDrawnGraphs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import edu.ForceDrawnGraphs.functions.FindWMEntDocumentFromTitleQuery;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.models.MWEntityDocumentProcessor;


@ShellComponent
public class InitGraphset implements FindWMEntDocumentFromTitleQuery {
  private static WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
  private static MWEntityDocumentProcessor docProc = new MWEntityDocumentProcessor();
  // private static EntityDocumentProcessorBroker edpb = new EntityDocumentProcessorBroker();

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("initGraphset(" + target + ") in InitGraphset.java");
    EntityDocument doc = findWMEntDocumentFromQuery(target, wbdf);
    docProc.ingestEntityDocument(doc);
  }
}
