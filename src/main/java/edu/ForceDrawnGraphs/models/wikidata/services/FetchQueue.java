package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
import java.util.Set;

import edu.ForceDrawnGraphs.models.wikidata.models.WikiDataEdge;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Line at the DMV but for QID strings retrieved during the ingest process.
 */

public class FetchQueue {
  private Set<StringTarget> stringQueue;
  private Set<EntityTarget> entityQueue;
  private Set<PropertyTarget> propertyQueue;
  private Set<ValueTarget> fetchedValues;

  public FetchQueue() {
    stringQueue = new HashSet<>();
    entityQueue = new HashSet<>();
    propertyQueue = new HashSet<>();
    fetchedValues = new HashSet<>();
  }

  //UTIL--------------------------------------------------------------------------------------------------------

  public boolean hasItems() {
    return !stringQueue.isEmpty() && !entityQueue.isEmpty() && !propertyQueue.isEmpty();
  }

  public List<String> getStringQueue() {
    return stringQueue.stream().map(item -> item.string).toList();
  }

  public List<String> getEntQueue() {
    return entityQueue.stream().map(item -> item.QID).toList();
  }

  public List<String> getPropQueue() {
    return propertyQueue.stream().map(item -> item.QID).toList();
  }

  /**
   * @returns a list of both entity and property QID values.
   */
  public List<String> getQIDQueue() {
    List<String> qids = new ArrayList<>();
    qids.addAll(getPropQueue());
    qids.addAll(getEntQueue());
    return qids;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS // PUBLIC METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  /**
  * Adds the details of a WikiDataEdge to the fetchQueue for further processing.
  * 
  * @param edge WikiDataEdge to add details to the fetchQueue.
  * 
  */
  public void addWikiDataEdgeDetails(WikiDataEdge edge) {

    String propQID = edge.propertyQID();
    String tgtVertQID = edge.tgtVertexQID();
    String value = edge.value();

    if (valueFetched(propQID) || !queueContainsProperty(propQID))
      addPropertyToQueue(edge.propertyQID());

    if (edge.tgtVertexQID() != null) {
      if (valueFetched(tgtVertQID) || !queueContainsEntity(tgtVertQID))
        addEntityToQueue(edge.tgtVertexQID());
    }

    if (edge.value() != null) {
      if (valueFetched(value) || !queueContainsString(value))
        addStringToQueue(value);
    }
  }

  public void entFetchSuccessful(String fetchedEntQID) {
    entityQueue.removeIf(item -> item.QID.equals(fetchedEntQID));
    propertyQueue.removeIf(item -> item.QID.equals(fetchedEntQID));
    stringQueue.removeIf(item -> item.string.equals(fetchedEntQID));

    fetchedValues.add(new ValueTarget(fetchedEntQID));
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

  private boolean valueFetched(String value) {
    return fetchedValues.stream().anyMatch(item -> item.value.equals(value));
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* RECORDS FOR QUEUE ITEMS - RECORDS FOR QUEUE ITEMS - RECORDS FOR QUEUE ITEMS - RECORDS FOR QUEUE ITEMS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private record StringTarget(String string) {
  }

  private record EntityTarget(String QID) {
  }

  private record PropertyTarget(String QID) {
  }

  private record ValueTarget(String value) {
  }
}
