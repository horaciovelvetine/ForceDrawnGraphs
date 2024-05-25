package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.ArrayList;

/**
 * Line at the DMV but for QID strings retrieved during the ItemDocument ingest process. Provides helpers for managing the queue, and verifying the graphset data.
 */

public class FetchQueue {
  // QIDs which have been referenced from another Vertex object but not yet fetched
  private ArrayList<QueueItem> unfetchedEnts;
  // QIDs which have been fetched, to avoid fetching them again/verify they have correctly made their way into the graphset
  private ArrayList<QueueItem> fetchedEnts;

  public FetchQueue() {
    this.unfetchedEnts = new ArrayList<QueueItem>();
    this.fetchedEnts = new ArrayList<QueueItem>();
  }

  public boolean isEmpty() {
    return this.unfetchedEnts.isEmpty();
  }

  public void addItem(String srcEntQID, String queryQID) {
    this.unfetchedEnts.add(new QueueItem(srcEntQID, queryQID));
  }

  public void clearFetchedEnts() {
    this.fetchedEnts.clear();
  }
  
  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  /**
   * Stores QID info immutably for later fetching.
   */

  private record QueueItem(String srcEntQID, String queryQID) {
  }
}
