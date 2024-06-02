package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Vertex;

/**
 * A class to process the entity documents returned from the MediaWiki API and interact with the main Graphset.
 */
public class EntDocProc implements Reportable {
  private final Graphset graphset; // The Graphset - central data structure for the application

  /**
   * Constructs an EntDocProc to process entity documents and interact with the Graphset.
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
   * @param entDocument the EntityDocument to be ingested.
   */
  public void processEntDocument(EntityDocument entDocument) {
    if (entDocument instanceof ItemDocument) {
      processItemDocument((ItemDocument) entDocument);
    } else if (entDocument instanceof PropertyDocument) {
      // processPropertyDocument((PropertyDocument) entDocument);
    } else {
      report("processEntDocument() unhandled document type:", entDocument.getClass().getName());
    }
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * Creates a new Vertex from the ItemDocument and adds it to the Graphset.
   * Then processes each of the ItemDocument's statements to add edges to the Graphset,
   * and new ItemQIDs to the WikiDocFetchQueue for lookup.
   *
   * @param itemDoc the ItemDocument to be processed.
   */
  private void processItemDocument(ItemDocument itemDoc) {
    Vertex vertex = new Vertex(itemDoc);
    graphset.addVertex(vertex);
    processItemForEdges(itemDoc);
  }

  /**
   * Processes each of the ItemDocument's statements to create edges in the Graphset.
   *
   * @param itemDoc the ItemDocument to be processed.
   */
  private void processItemForEdges(ItemDocument itemDoc) {
    String srcItemQID = itemDoc.getEntityId().getId();
    List<StmtDetailsProcessor> filteredStmts = filterAllStatementsForRelevantInfo(itemDoc.getAllStatements());
    for (StmtDetailsProcessor stmt : filteredStmts) {
      stmt.createEdgesFromStmtDetails(srcItemQID);
      graphset.addEdgesAndUpdateQueues(stmt.edges());
    }
  }

  /**
   * Filters all statements in the ItemDocument for relevant information to create edges.
   *
   * @param statements the iterator of statements to be processed.
   * @return A list of StmtDetailsProcessor containing relevant statements.
   */
  private List<StmtDetailsProcessor> filterAllStatementsForRelevantInfo(Iterator<Statement> statements) {
    List<StmtDetailsProcessor> filteredStmts = new ArrayList<>();
    while (statements.hasNext()) {
      StmtDetailsProcessor stmt = new StmtDetailsProcessor(statements.next());
      if (!stmt.definesIrrelevantOrExternalInfo()) {
        filteredStmts.add(stmt);
      }
    }
    return filteredStmts;
  }
}
