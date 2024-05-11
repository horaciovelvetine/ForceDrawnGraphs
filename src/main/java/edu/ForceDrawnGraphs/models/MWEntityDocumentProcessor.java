package edu.ForceDrawnGraphs.models;

import java.util.Iterator;

import org.eclipse.rdf4j.model.util.Statements;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.util.NestedIterator;

import edu.ForceDrawnGraphs.interfaces.Reportable;

public class MWEntityDocumentProcessor implements Reportable {
  /**
   * Default Constructor.
   */
  public MWEntityDocumentProcessor() {
    //Default
  }

  private void processItemDocument(ItemDocument document) {
    // Handle our items here
    String label = document.findLabel("en");
    String desc = document.findDescription("en");
    String QID = document.getEntityId().getId();

    Iterator<Statement> sit = document.getAllStatements();
    print("Process Item");
  }

  /**
   * Ingests and handles the return results when querying the MediaWiki API by passing the entity document to the correct processor based on the type of document. Includes some handling 
   */
  public void ingestEntityDocument(EntityDocument document) {
    if (document instanceof ItemDocument) {
      processItemDocument((ItemDocument) document);
    } else {
      report("Encountered new DocType: " + document.getClass().getName());
    }

  }
}
