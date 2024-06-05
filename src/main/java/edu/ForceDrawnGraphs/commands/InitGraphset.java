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
  private static APIBroker wikiDataAPIBroker = new APIBroker(docProc, graphset);
  private int fetchDepth = 2;

  @ShellMethod("Create a graphset (JSON file) given an origin target (or a default of Kevin Bacon).")
  public void ig(@ShellOption(defaultValue = "Kevin Bacon") String target) {
    graphset.setOriginQuery(target); // set the origin query for the graphset
    wikiDataAPIBroker.fuzzyFetchOriginEntityDocument(target); // fetch origin entity document, and init graphset data management

    while (fetchDepth-- >= 0 && graphset.wikiDataFetchQueue().hasItems()) {
      // fetcch any details for items in the queue // QIDs, Props, & Strings
      wikiDataAPIBroker.fetchQueuedValuesDetails();

      //TODO !!!!!!!!!STOP DROP AND CODE
      // Q/P Ents which have the same label and are meant to represent the same thing "P1963" properties for this type "when this subject is used as object of "instance of" the following props normally apply

      //Testing of structure with n-1 completed: 
      // record & report values: statements.size(), filteredStatements.size(), through method chain. Verify no unexpected/outliers - also would give some interesting data


      // init a grouping system to reference in the future, that way any edge providing context to another, and its children can be tied kept together
      // "P7084" related category - this may be a smart case to handle seperately

      // "Q345165" (Wikimedia disambiguation page) // "P7084" (related category) // "P4224" (category contains) Wikimedia disambiguation page - similar case to handle 

      // rn properties in the entityQueue happen because they are the tgtValue given - this may be a handy check to add an extra search value (the label of this result) to the query queue (to find its inevitable Vertex twin)

      // "P31" def - "(instance of) that class of which this subject is a particular example and member; different from "P279" (subclass of);" example [joan of arc] is a [human] - where there is 2 edges to define this sort of pattern (which is very common)
      // Should look for an edge (which has snakType "QUALIFIER") and a property QID of "P11527" and a tgtVertexID of "P31" - it may be possible to omit edges with a tgtValueID of "P31"}"

      // my handling of string and quant vlaues may have been harsh, outright rejecting all of them - they could be valuable qualifier info... and not an issue there (just no good as a main snak)

      // might be handy to have a group size stat in addition to the groupID
    }
    print("Stop graphset end init.");
  }
}
