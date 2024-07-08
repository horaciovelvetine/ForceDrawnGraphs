package edu.ForceDrawnGraphs.wikidata.services;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.models.Property;
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
      processPropDocument((PropertyDocument) entDocument);
    } else {
      report("processEntDocument() unhandled document type:", entDocument.getClass().getName());
    }
  }

  /**
   * Processes the results of a date search query, logging the result.
   *
   * @param dateResult the result of the date search query.
   * @param queryVal the query value used to store and query the WD API.
   */
  public void processDateResult(WbSearchEntitiesResult dateResult, String queryVal) {
    Vertex dateVertex = new Vertex(dateResult);
    graphset.addVertexToLookup(dateVertex);
    graphset.assignDateVertexToEdges(dateVertex, queryVal);
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------


  private void processPropDocument(PropertyDocument propertyDocument) {
    Property p = new Property(propertyDocument);
    graphset.addPropToLookup(p);
  }

  private void processItemDocument(ItemDocument itemDoc) {
    Vertex vertex = new Vertex(itemDoc);
    graphset.addVertexToLookup(vertex);

    CompletableFuture.runAsync(() -> {
      processItemForEdges(itemDoc);
    }).exceptionally(ex -> {
      report("processItemForEdges() error: " + ex.getMessage());
      return null;
    });
  }

  private void processItemForEdges(ItemDocument itemDoc) {
    String srcItemQID = itemDoc.getEntityId().getId();
    List<StmtProc> filteredStmts = filterAllStatementsForRelevantInfo(itemDoc.getAllStatements());

    for (StmtProc stmt : filteredStmts) {
      stmt.createEdgesFromStmtDetails(srcItemQID);
      graphset.addEdgesToLookupAndQueue(stmt);
    }
  }

  private List<StmtProc> filterAllStatementsForRelevantInfo(Iterator<Statement> statements) {
    List<StmtProc> filteredStmts = new ArrayList<>();

    while (statements.hasNext()) {
      StmtProc stmt = new StmtProc(statements.next());
      if (!stmt.definesIrrelevantOrExternalInfo()) {
        filteredStmts.add(stmt);
      }
    }
    return filteredStmts;
  }

}
