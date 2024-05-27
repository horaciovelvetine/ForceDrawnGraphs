package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Vertex;

/**
 * A class to process the entity documents returned from the MediaWiki API and interact with the main Graphset.
 * 
 * @param graphset Graphset notified of all object updates and changes.
 */
public class EntDocProc implements Reportable {
  private Graphset graphset; // The Graphset - central data structure for the application

  /**
   * A class to process the entity documents returned from the MediaWiki API and interact with the main Graphset.
   * 
   * @param graphset Graphset notified of all object updates and changes.
   */
  public EntDocProc(Graphset graphset) {
    this.graphset = graphset;
  }

  /**
   * Ingests Entity Document results from the Wikimedia API, type narrows, and directs them to the appropriate processing method.
   * For documents it does not recognize, it logs a message containing that type and throws an error to allow for handling. 
   *
   * @param entDocument the EntityDocument to be ingested
   */
  public void processEntDocument(EntityDocument entDocument) {

    if (entDocument instanceof ItemDocument) {
      processItemDocument((ItemDocument) entDocument);
    } else {
      String docType = entDocument.getClass().getName();
      report("Encountered new DocType: " + docType);
      throw new IllegalArgumentException("Unhandled Document type: " + docType);
    }

  }

  /**
   * Creates a new Vertex from the ItemDocument and adds it to the Graphset. 
   * Then processes each of the ItemDocument's statements to add edges to the Graphset,
   * and new ItemQIDs to the WikiDocFetchQueue for lookup.
   * 
   * @param itemDoc the ItemDocument to be processed
   */
  private void processItemDocument(ItemDocument itemDoc) {
    Vertex vertex = new Vertex(itemDoc);
    // graphset.addVertex(vertex);

    processItemStatementsForEdges(itemDoc, vertex);

    print("Backstop: Ends here.");
  }

  /**
   * Processes each of the ItemDocument's statements to create edges in the Graphset.
   * Statements are broken into their component snaks: mainSnak, qualifiers, and references  
   * then checked for relevant values to create edges. Irrelevant values are ignored.
   * 
   * @param itemDoc the ItemDocument to be processed
   */
  private void processItemStatementsForEdges(ItemDocument itemDoc, Vertex srcVertex) {
    List<StmtDetailsProcessor> filteredStmts = filterAllStatmentsForRelevantInfo(itemDoc);
    List<Edge> allNewEdges = new ArrayList<>();

    for (StmtDetailsProcessor stmt : filteredStmts) {
      List<Edge> newEdges = stmt.createEdgesFromDetails(srcVertex);
      allNewEdges.addAll(newEdges);
    }

    //TODO: back here, add edges to the graphset, and any new EntQIDs to the WikiDocFetchQueue
    print("Items Statements stop.");
  }

  /**
   * Filters all statements in the ItemDocument for relevant information to create edges.
   * 
   * @param itemDoc the ItemDocument to be processed
   */
  private List<StmtDetailsProcessor> filterAllStatmentsForRelevantInfo(ItemDocument itemDoc) {
    Iterator<Statement> statements = itemDoc.getAllStatements();
    List<StmtDetailsProcessor> filteredStmts = new ArrayList<>();

    while (statements.hasNext()) {
      Statement statement = statements.next();
      StmtDetailsProcessor stmt = new StmtDetailsProcessor(statement);

      if (stmt.definesIrrelevantOrExternalInfo()) {
        // external source, skip irrelevant values
        continue;
      }
      // possible edge, add to filteredStmts
      filteredStmts.add(stmt);
    }

    return filteredStmts;
  }

}
