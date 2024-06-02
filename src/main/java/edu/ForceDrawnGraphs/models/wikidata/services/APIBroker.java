package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.util.ProcessTimer;

public class APIBroker implements Reportable {
  private WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
  private EntDocProc docProc; // The processor to notify when a document is found

  public APIBroker(EntDocProc docProc) {
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

    EntityDocument docResult = fetchEntityDocByTitleQuery(target); // Try to fetch by title query

    if (docResult == null) { // If no result is found, try to fetch by site query
      docResult = fetchEntityDocBySiteQuery(target);
    }

    if (docResult != null) { // If a result is found, notify the processor and ingest the document
      docProc.processEntDocument(docResult);
    } else { // If no result is found, log it
      //TODO: Add a catch for when no result is found
      log("fuzzyFetchOriginEntityDocument() no result found for target: " + target);
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
   * Fetches a list of Entity documents by their QIDs.
   * 
   * @param qids The list of QIDs to fetch.
   * 
   * @return A map of QIDs to EntityDocuments, or null if an error occurred.
   */
  private Map<String, EntityDocument> fetchEntitiesByQIDs(List<String> qids) {
    try {
      return wbdf.getEntityDocuments(qids);
    } catch (MediaWikiApiErrorException e) {
      log("fetchEntitiesByQIDs() unable to access the Media Wiki API:", e);
    } catch (Exception e) {
      log("fetchEntitiesByQIDs() error:", e);
    }
    return null;
  }

}
