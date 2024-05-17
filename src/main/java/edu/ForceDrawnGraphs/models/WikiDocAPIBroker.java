package edu.ForceDrawnGraphs.models;

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import edu.ForceDrawnGraphs.interfaces.ProcessTimer;
import edu.ForceDrawnGraphs.interfaces.Reportable;

public class WikiDocAPIBroker implements Reportable {
  private WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
  private WikiDocProcessor docProc; // The processor to notify when a document is found

  public WikiDocAPIBroker(WikiDocProcessor docProc) {
    this.docProc = docProc;
  }

  /**
   * Fetches an Entity document using fuzzy matching based on the target.
   * If no exact match is found, it tries to widen the search using a site query. 
   * The resultant document is ingested and set as the origin document for the Graphset.
   *
   * @param target the query string used to search for the entity document.
   */
  public void fuzzyFetchOriginEntityDocument(String target) {
    ProcessTimer timer = new ProcessTimer(
        "fuzzyFetchOriginEntityDocument(" + target + ") in EntityDocFetchBroker.java");

    EntityDocument docResult = fetchEntityDocByTitleQuery(target); // Try to fetch by title query

    if (docResult == null) { // If no result is found, try to fetch by site query
      docResult = fetchEntityDocBySiteQuery(target);
    }

    if (docResult != null) { // If a result is found, notify the processor and ingest the document
      docProc.ingestEntityDocument(docResult);
    } else { // If no result is found, log it
      log("fuzzyFetchOriginEntityDocument() no result found for target: " + target);
    }
    timer.end();
    //TODO: Add a catch for when no result is found
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
    ProcessTimer timer = new ProcessTimer("fetchEntityDocByTitleQuery(" + query + ") in EntityDocFetchBroker.java");

    try {
      return wbdf.getEntityDocumentByTitle("enwiki", query);
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntityDocByTitleQuery() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntityDocByTitleQuery() error:", e);
    } finally {
      timer.end();
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
    ProcessTimer timer = new ProcessTimer("fetchEntityDocBySiteQuery(" + query + ") in EntityDocFetchBroker.java");

    try {
      List<WbSearchEntitiesResult> searchResults = wbdf.searchEntities(query, "en");
      if (searchResults.size() > 0) { // results are sorted by relevance, so we take the first one
        return wbdf.getEntityDocument(searchResults.get(0).getEntityId());
      }
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntityDocBySiteQuery() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntityDocBySiteQuery() error:", e);
    } finally {
      timer.end();
    }
    return null;
  }

  /**
   * Fetches a list of Entity documents by their QIDs.
   * 
   * @param qids The list of QIDs to fetch.
   * 
   * @return A map of QIDs to EntityDocuments, or null if an error occurred.
   */
  private Map<String, EntityDocument> fetchEntitiesByQIDs(List<String> qids) {
    ProcessTimer timer = new ProcessTimer(
        "fetchEntitiesByQIDs(" + qids.size() + " total fetched) in EntityDocFetchBroker.java");

    try {
      return wbdf.getEntityDocuments(qids);
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntitiesByQIDs() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntitiesByQIDs() error:", e);
    } finally {
      timer.end();
    }
    return null;
  }

}
