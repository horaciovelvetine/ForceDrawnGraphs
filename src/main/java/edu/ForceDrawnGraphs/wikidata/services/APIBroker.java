package edu.ForceDrawnGraphs.wikidata.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Graphset;
import edu.ForceDrawnGraphs.util.ProcessTimer;

public class APIBroker implements Reportable {
  private WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
  private EntDocProc docProc;
  private Graphset graphset;

  public APIBroker(EntDocProc docProc, Graphset graphset) {
    this.docProc = docProc;
    this.graphset = graphset;
  }

  public void fuzzyFetchOriginEntityDocument(String target) {
    EntityDocument docResult = fetchEntityDocument(target);
    if (docResult != null) {
      processDocument(docResult);
    } else {
      report("No result found for target: " + target);
    }
  }

  public void fetchQueuedValuesDetails() {
    ExecutorService executor = Executors.newFixedThreadPool(3);
    try {
      CompletableFuture<Void> allFutures = startFetchTasks(executor);
      allFutures.join();
    } finally {
      executor.shutdown();
      new ProcessTimer("Total Ents Fetched: ").end();
    }
  }

  private CompletableFuture<Void> startFetchTasks(ExecutorService executor) {
    int depth = graphset.depth();
    CompletableFuture<Integer> entFuture =
        CompletableFuture.supplyAsync(() -> fetchAndProcessEntities(depth), executor);
    CompletableFuture<Integer> propFuture =
        CompletableFuture.supplyAsync(() -> fetchAndProcessProperties(depth), executor);
    CompletableFuture<Integer> dateFuture =
        CompletableFuture.supplyAsync(() -> fetchAndProcessDates(depth), executor);

    return CompletableFuture.allOf(entFuture, propFuture, dateFuture);
  }


  private EntityDocument fetchEntityDocument(String query) {
    EntityDocument doc = fetchEntityDocByTitleQuery(query);
    return doc != null ? doc : fetchEntityDocBySiteQuery(query);
  }

  private EntityDocument fetchEntityDocByTitleQuery(String query) {
    try {
      return wbdf.getEntityDocumentByTitle("enwiki", query);
    } catch (Exception e) {
      log("fetchEntityDocByTitleQuery error: ", e);
      return null;
    }
  }

  private EntityDocument fetchEntityDocBySiteQuery(String query) {
    try {
      List<WbSearchEntitiesResult> searchResults = wbdf.searchEntities(query, "en");
      if (!searchResults.isEmpty()) {
        return wbdf.getEntityDocument(searchResults.get(0).getEntityId());
      }
    } catch (Exception e) {
      log("fetchEntityDocBySiteQuery error: ", e);
    }
    return null;
  }

  private void processDocument(EntityDocument doc) {
    docProc.processEntDocument(doc);
    graphset.wikiDataFetchQueue().fetchSuccessful(doc.getEntityId().getId());
  }

  private Integer fetchAndProcessEntities(int depth) {
    List<String> entQIDs = graphset.wikiDataFetchQueue().getEntityQueue(depth);
    if (entQIDs.isEmpty())
      return 0;

    try {
      Map<String, EntityDocument> docMap = wbdf.getEntityDocuments(entQIDs);
      List<EntityDocument> docs = new ArrayList<>(docMap.values());

      docs.forEach(doc -> {
        graphset.wikiDataFetchQueue().fetchSuccessful(doc.getEntityId().getId());
        docProc.processEntDocument(doc);
      });

      return docs.size();
    } catch (Exception e) {
      log("fetchAndProcessEntities() error: ", e);
    }
    return 0;
  }

  private Integer fetchAndProcessProperties(int depth) {
    List<String> propQIDs = graphset.wikiDataFetchQueue().getPropertyQueue(depth);
    if (propQIDs.isEmpty())
      return 0;
    try {
      Map<String, EntityDocument> docMap = wbdf.getEntityDocuments(propQIDs);
      List<EntityDocument> docs = new ArrayList<>(docMap.values());

      docs.forEach(doc -> {
        graphset.wikiDataFetchQueue().fetchSuccessful(doc.getEntityId().getId());
        docProc.processEntDocument(doc);
      });
      return docs.size();
    } catch (Exception e) {
      log("fetchAndProcessProperties() error: ", e);
    }
    return 0;
  }

  private Integer fetchAndProcessDates(int depth) {
    List<String> dateVals = graphset.wikiDataFetchQueue().getStringQueue(depth);
    if (dateVals.isEmpty())
      return 0;

    dateVals.forEach(date -> {
      try {
        WbSearchEntitiesResult result = wbdf.searchEntities(date, "en").get(0);
        graphset.wikiDataFetchQueue().fetchSuccessful(date);
        docProc.processDateResult(result, date);
      } catch (Exception e) {
        log("Error processing date: " + date, e);
      }
    });
    return dateVals.size();
  }
}
