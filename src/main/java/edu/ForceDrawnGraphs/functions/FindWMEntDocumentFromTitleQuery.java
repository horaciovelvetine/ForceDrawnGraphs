package edu.ForceDrawnGraphs.functions;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.interfaces.Reportable;

/**
 * This interface represents a function to find a Wikidata entity document
 * based on a title query.
 */
public interface FindWMEntDocumentFromTitleQuery extends Reportable {

  /**
   * Finds a Wikidata entity document based on the given query using the
   * provided WikibaseDataFetcher.
   *
   * @param query The title query to search for.
   * @param wbdf The WikibaseDataFetcher instance to use for fetching the entity document.
   * @return The found EntityDocument, or null if not found or an error occurred.
   */
  public default EntityDocument findWMEntDocumentFromQuery(String query, WikibaseDataFetcher wbdf) {
    ProcessTimer timer = new ProcessTimer(
        "findWMEntDocumentFromQuery(" + query + ") in FindWMEntDocumentFromTitleQuery.java");
    EntityDocument result = null;
    try {
      result = wbdf.getEntityDocumentByTitle("enwiki", "Kevin Bacon");
    } catch (Exception e) {
      report("FindWMEntDocumentFromQuery().java error:", e);
    } finally {
      timer.end();
    }
    return result;
  }
}
