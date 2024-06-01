package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
import java.util.ArrayList;

/**
 * Line at the DMV but for QID strings retrieved during the ingest process.
 */

public class FetchQueue {
  // <K=TYPE (QID/QUERY), V=QUERY_TEXT>
  private List<QueueItem> queue;

  public FetchQueue() {
    queue = new ArrayList<>();
  }

  public void addEntityQIDToQueue(String query) {
    queue.add(new QueueItem(query));
  }

  public boolean hasItems() {
    return !queue.isEmpty();
  }

  public QueueItem nextItem() {
    return queue.remove(0);
  }

  public String totalItemsInQueue() {
    return String.valueOf(queue.size());
  }

  public boolean queueContainsQuery(String query) {
    return queue.stream().anyMatch(item -> item.query.equals(query));
  }

  public List<QueueItem> queue() {
    return queue;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  public record QueueItem(String query) {
  }
}
