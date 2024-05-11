package edu.ForceDrawnGraphs.commands;

import javax.swing.text.html.parser.Entity;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import edu.ForceDrawnGraphs.functions.FindWMEntDocumentFromTitleQuery;
import edu.ForceDrawnGraphs.interfaces.ProcessTimer;

@ShellComponent
public class InitGraphset implements FindWMEntDocumentFromTitleQuery {
  private static WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void initGraphset(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    ProcessTimer timer = new ProcessTimer("initGraphset(" + target + ") in InitGraphset.java");
    EntityDocument entityDocument = findWMEntDocumentFromQuery(target, wbdf);
    print("Stops here");
  }
}
