package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;

import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge;

import java.util.ArrayList;

/**
 * Line at the DMV but for QID strings retrieved during the ingest process.
 */

public class FetchQueue {
  private List<StringTarget> stringQueue;
  private List<EntityTarget> entityQueue;
  private List<PropertyTarget> propertyQueue;

  public FetchQueue() {
    stringQueue = new ArrayList<>();
    entityQueue = new ArrayList<>();
    propertyQueue = new ArrayList<>();
  }

  public boolean hasItems() {
    return !stringQueue.isEmpty() && !entityQueue.isEmpty() && !propertyQueue.isEmpty();
  }

  /**
  * Adds the details of a WikiDataEdge to the fetchQueue for further processing.
  * 
  * @param edge WikiDataEdge to add details to the fetchQueue.
  * 
  */
  public void addWikiDataEdgeDetailsToQueue(WikiDataEdge edge) {
    // add propertyQID to fetchQueue if not already present
    if (!queueContainsProperty(edge.propertyQID()))
      addPropertyToQueue(edge.propertyQID());

    if (edge.tgtVertexQID() != null) {
      // add tgtVertexQID to fetchQueue if not already present
      if (!queueContainsEntity(edge.tgtVertexQID()))
        addEntityToQueue(edge.tgtVertexQID());
    }

    if (edge.value() != null) {
      // add value to fetchQueue if not already present
      if (!queueContainsString(edge.value()))
        addStringToQueue(edge.value());
    }
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private void addStringToQueue(String string) {
    stringQueue.add(new StringTarget(string));
  }

  private boolean queueContainsString(String string) {
    return stringQueue.stream().anyMatch(item -> item.string.equals(string));
  }

  private void addEntityToQueue(String QID) {
    entityQueue.add(new EntityTarget(QID));
  }

  private boolean queueContainsEntity(String QID) {
    return entityQueue.stream().anyMatch(item -> item.QID.equals(QID));
  }

  private void addPropertyToQueue(String QID) {
    propertyQueue.add(new PropertyTarget(QID));
  }

  private boolean queueContainsProperty(String QID) {
    return propertyQueue.stream().anyMatch(item -> item.QID.equals(QID));
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* RECORDS FOR QUEUE ITEMS - RECORDS FOR QUEUE ITEMS - RECORDS FOR QUEUE ITEMS - RECORDS FOR QUEUE ITEMS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  public record StringTarget(String string) {
  }

  public record EntityTarget(String QID) {
  }

  public record PropertyTarget(String QID) {
  }
}
