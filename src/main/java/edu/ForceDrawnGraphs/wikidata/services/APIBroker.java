package edu.ForceDrawnGraphs.wikidata.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;

public class APIBroker implements Reportable {
  private WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
  private EntDocProc docProc; // The processor to notify when a document is found
  private Graphset graphset; // The graphset to update with the found document

  public APIBroker(EntDocProc docProc, Graphset graphset) {
    this.docProc = docProc;
    this.graphset = graphset;
  }

  /**
   * Fetches an Entity document using fuzzy matching based on the target.
   * If no exact match is found, it tries to widen the search using a site query. 
   * The resultant document is ingested and set as the origin document for the Graphset.
   *
   * @param target the query string used to search for the entity document.
   */
  public void fuzzyFetchOriginEntityDocument(String target) {

    EntityDocument docResult = fetchEntityDocByTitleQuery(target); // Try to fetch by title query

    if (docResult == null) { // If no result is found, try to fetch by site query
      docResult = fetchEntityDocBySiteQuery(target);
    }

    if (docResult != null) { // If a result is found, notify the processor and ingest the document
      docProc.processEntDocument(docResult);
      graphset.wikiDataFetchQueue().fetchSuccessful(docResult.getEntityId().getId());
    } else { // If no result is found, log it
      report("fuzzyFetchOriginEntityDocument() no result found for target: " + target);
    }
  }

  /**
   * Fetches the details of all queued values in the Graphset's WikiDataFetchQueue. (from the most recently imported batch of processed statements)
   * This method fetches all queued strings and QIDs, and processes the resultant Entities (Items, Properties, Strings, Dates, and Quantities).
   *
   * @param graphset The Graphset containing the WikiDataFetchQueue.
   */
  public void fetchQueuedValuesDetails(Integer depth) {
    //TODO STRING VALUE PROCESSING
    ExecutorService executor = Executors.newFixedThreadPool(2);

    CompletableFuture<Void> entFuture =
        CompletableFuture.runAsync(() -> fetchAndProcessEntDocs(depth), executor);
    CompletableFuture<Void> propFuture =
        CompletableFuture.runAsync(() -> fetchAndProcessPropDocs(depth), executor);

    CompletableFuture.allOf(entFuture, propFuture).join();
    print("Depth fetch complete " + depth);
    executor.shutdown();
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private void fetchAndProcessPropDocs(Integer depth) {
    Map<String, EntityDocument> docs =
        fetchEntitiesByQIDs(graphset.wikiDataFetchQueue().getPropertyQueue(depth));
    for (EntityDocument propDoc : docs.values()) {
      graphset.wikiDataFetchQueue().fetchSuccessful(propDoc.getEntityId().getId());
      docProc.processEntDocument(propDoc);
    }
  }

  private void fetchAndProcessEntDocs(Integer depth) {
    Map<String, EntityDocument> docs =
        fetchEntitiesByQIDs(graphset.wikiDataFetchQueue().getEntityQueue(depth));
    for (EntityDocument entDoc : docs.values()) {
      graphset.wikiDataFetchQueue().fetchSuccessful(entDoc.getEntityId().getId());
      docProc.processEntDocument(entDoc);
    }
  }

  /**
   * Fetch an EntityDocument by querying Entitiy labels for the provided (default: "enwiki") wiki.
   * 
   * @param query The query to search for.
   * 
   * @return The found EntityDocument, or null if not found or an error occurred.
   */
  private EntityDocument fetchEntityDocByTitleQuery(String query) {
    try {
      return wbdf.getEntityDocumentByTitle("enwiki", query);
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntityDocByTitleQuery() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntityDocByTitleQuery() error:", e);
    }
    return null;
  }

  /**
   * Fetch an EntityDocument by a site (default IRI "en" used) and query. This query searches all entities of the provided Wiki by their entityID, label, or alias.
   * 
   * @param query The query to search for.
   * 
   * @return The found EntityDocument, or null if not found or an error occurred.
   */
  private EntityDocument fetchEntityDocBySiteQuery(String query) {
    try {
      List<WbSearchEntitiesResult> searchResults = wbdf.searchEntities(query, "en");
      if (searchResults.size() > 0) { // results are sorted by relevance, so we take the first one
        return wbdf.getEntityDocument(searchResults.get(0).getEntityId());
      }
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntityDocBySiteQuery() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntityDocBySiteQuery() error:", e);
    }
    return null;
  }

  /**
   * Fetches a list of Entity documents by their QIDs, batches any request with more than batchSize QIDs.
   * 
   * @param qids The list of QIDs to fetch.
   * 
   * @return A map of QIDs to EntityDocuments, or null if an error occurred.
   * @throws Exception 
   */
  private Map<String, EntityDocument> fetchEntitiesByQIDs(List<String> qids) {
    Map<String, EntityDocument> result = new HashMap<>();
    int batchSize = 50;

    for (int i = 0; i < qids.size(); i += batchSize) {
      int end = Math.min(i + batchSize, qids.size());
      List<String> batch = qids.subList(i, end);

      try {
        result.putAll(wbdf.getEntityDocuments(batch));
      } catch (Exception e) {
        log("fetchEntitiesByQIDs() error:", e);
        // establish some sort of dead node, or type of vertex that leads to incomplete or errored data
      }
    }

    return result;
  }

}
