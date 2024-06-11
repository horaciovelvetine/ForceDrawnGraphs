package edu.ForceDrawnGraphs.wikidata.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.util.ProcessTimer;

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

  public void fetchQueuedValuesDetails() {
    int depth = graphset.depth();
    int entsCollected = 0;
    ProcessTimer timer = new ProcessTimer("queueFetch N=" + depth);

    ExecutorService executor = Executors.newFixedThreadPool(3);

    CompletableFuture<Integer> entFuture =
        CompletableFuture.supplyAsync(() -> fetchAndProcessEntities(depth), executor);
    CompletableFuture<Integer> propFuture =
        CompletableFuture.supplyAsync(() -> fetchAndProcessProperties(depth), executor);
    CompletableFuture<Integer> dateFuture =
        CompletableFuture.supplyAsync(() -> fetchAndProcessDates(depth), executor);

    CompletableFuture.allOf(entFuture, propFuture, dateFuture).join();
    executor.shutdown();

    try {
      entsCollected = entFuture.get() + propFuture.get() + dateFuture.get();
      report("Depth fetch complete fetched: " + entsCollected + ".\n" + graphset.toString());
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    } finally {
      timer.end(" Total Ents Fetched: ");
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
   * Fetch an EntityDocument by querying Entitiy labels for the provided (default: "enwiki") wiki.
   * 
   * @param query The query to search for.
   * 
   * @return The found EntityDocument, or null if not found or an error occurred.
   */

  private EntityDocument fetchEntityDocByTitleQuery(String query) {
    EntityDocument doc = null;

    try {
      return wbdf.getEntityDocumentByTitle("enwiki", query);
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntityDocByTitleQuery() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntityDocByTitleQuery() error:", e);
    }
    return doc;
  }

  /**
   * Fetch an EntityDocument by a site (default IRI "en" used) and query. This query searches all entities of the provided Wiki by their entityID, label, or alias.
   * 
   * @param query The query to search for.
   * 
   * @return The found EntityDocument, or null if not found or an error occurred.
   */
  private EntityDocument fetchEntityDocBySiteQuery(String query) {
    EntityDocument doc = null;

    try {
      List<WbSearchEntitiesResult> searchResults = wbdf.searchEntities(query, "en");
      if (searchResults.size() > 0) {
        return wbdf.getEntityDocument(searchResults.get(0).getEntityId());
      }
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntityDocBySiteQuery() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntityDocBySiteQuery() error:", e);
    }
    return doc;
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

    try {
      result.putAll(wbdf.getEntityDocuments(qids));
    } catch (Exception e) {
      log("fetchEntitiesByQIDs() error: ", e);
      // establish some sort of dead node, or type of vertex that leads to incomplete or errored data
    }

    return result;
  }

  /**
   * Fetches a list of Entity documents by their labels, batches any request with more than batchSize labels.
   * 
   * @param labels The list of labels to fetch.
   * 
   * @return A map of labels to EntityDocuments, or null if an error occurred.
   */

  private Map<String, EntityDocument> fetchEntitiesByLabels(List<String> labels) {
    Map<String, EntityDocument> result = new HashMap<>();

    try {
      result.putAll(wbdf.getEntityDocumentsByTitle("en", labels));
    } catch (Exception e) {
      log("fetchEntitiesByLabels() error: ");
    }
    return result;
  }



  private Integer fetchAndProcessProperties(Integer depth) {
    Map<String, EntityDocument> docs =
        fetchEntitiesByQIDs(graphset.wikiDataFetchQueue().getPropertyQueue(depth));
    processSuccessfullyFetchedEnts(docs);
    return docs.size();
  }

  private Integer fetchAndProcessEntities(Integer depth) {
    Map<String, EntityDocument> docs =
        fetchEntitiesByQIDs(graphset.wikiDataFetchQueue().getEntityQueue(depth));
    processSuccessfullyFetchedEnts(docs);
    return docs.size();
  }

  private Integer fetchAndProcessDates(Integer depth) {
    Map<String, EntityDocument> docs =
        fetchEntitiesByLabels(graphset.wikiDataFetchQueue().getStringQueue(depth));
    processSuccessfullyFetchedEnts(docs);
    return docs.size();
  }

  private void processSuccessfullyFetchedEnts(Map<String, EntityDocument> docs) {
    for (EntityDocument doc : docs.values()) {
      graphset.wikiDataFetchQueue().fetchSuccessful(doc.getEntityId().getId());
      docProc.processEntDocument(doc);
    }
  }

}
