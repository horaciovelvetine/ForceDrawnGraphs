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
   * @param itemDoc the ItemDocument to be processed
   */
  private void processItemDocument(ItemDocument itemDoc) {
    // VERTEX // NODE CREATED
    Vertex vertex = new Vertex(itemDoc);
    graphset.addVertex(vertex);
    // EDGE // CONNECTIONS CREATED
    processItemForEdges(itemDoc);
  }

  /**
   * Processes each of the ItemDocument's statements to create edges in the Graphset.  
   * 
   * @param itemDoc the ItemDocument to be processed
   */
  private void processItemForEdges(ItemDocument itemDoc) {
    String srcItemQID = itemDoc.getEntityId().getId();
    // filter out irrelevant statements
    List<StmtDetailsProcessor> filteredStmts = filterAllStatmentsForRelevantInfo(itemDoc.getAllStatements());
    // process filtered statements 
    for (StmtDetailsProcessor stmt : filteredStmts) {
      stmt.createEdgesFromStmtDetails(srcItemQID);
      // Edges contain unfetched Ent info, add edges to dataset, and add unfetched Ent info to FetchQueue
      graphset.addEdgesAndUpdateFetchQueue(stmt.edges());
    }
  }

  /**
   * Filters all statements in the ItemDocument for relevant information to create edges.
   * 
   * @param itemDoc the ItemDocument to be processed
   */
  private List<StmtDetailsProcessor> filterAllStatmentsForRelevantInfo(Iterator<Statement> statements) {
    List<StmtDetailsProcessor> filteredStmts = new ArrayList<>();

    while (statements.hasNext()) {
      StmtDetailsProcessor stmt = new StmtDetailsProcessor(statements.next());

      if (stmt.definesIrrelevantOrExternalInfo()) {
        // external source ==> skip!
        continue;
      }
      // possible edge, add to return list
      filteredStmts.add(stmt);
    }

    return filteredStmts;
  }

}
