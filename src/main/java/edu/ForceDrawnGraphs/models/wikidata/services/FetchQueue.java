package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
import java.util.ArrayList;

/**
 * Line at the DMV but for QID strings retrieved during the ingest process.
 */

public class FetchQueue {
  // <K=TYPE (QID/QUERY), V=QUERY_TEXT>
  private List<QueueItem> queryQueue;

  public FetchQueue() {
    queryQueue = new ArrayList<>();
  }

  public void addEntityQIDToQueue(String query) {
    queryQueue.add(new QueueItem(query));
  }

  public boolean hasItems() {
    return !queryQueue.isEmpty();
  }

  public QueueItem nextItem() {
    return queryQueue.remove(0);
  }

  public String totalItemsInQueue() {
    return String.valueOf(queryQueue.size());
  }

  public boolean queueContainsQuery(String query) {
    return queryQueue.stream().anyMatch(item -> item.query.equals(query));
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private record QueueItem(String query) {
  }
}
