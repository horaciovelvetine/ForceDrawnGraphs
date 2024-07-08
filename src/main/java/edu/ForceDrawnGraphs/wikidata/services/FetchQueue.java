package edu.ForceDrawnGraphs.wikidata.services;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.Edge;


/**
 * Manages separate queues for different data types,entities, properties, and strings (typically dates), this includes:
 * methods to add data to the queues, retrieve data from the queues, mark fetched items as successful, 
 * and check the presence of items at a specific depth in the queues.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FetchQueue implements Reportable {
  private Set<StrTarget> stringQueue;
  private Set<EntTarget> entityQueue;
  private Set<PropTarget> propertyQueue;
  private Set<FetchedTarget> fetchedValues;
  private Set<DeadTarget> deadValues;

  /**
   * Initializes new concurrent sets for each queue to handle concurrent modifications.
   */
  public FetchQueue() {
    this.entityQueue = ConcurrentHashMap.newKeySet();
    this.stringQueue = ConcurrentHashMap.newKeySet();
    this.propertyQueue = ConcurrentHashMap.newKeySet();
    this.fetchedValues = ConcurrentHashMap.newKeySet();
    this.deadValues = ConcurrentHashMap.newKeySet();
  }

  /**
   * Adds details from a WikiDataEdge to the appropriate queues if they are not already present.
   * 
   * @param newEdge the WikiDataEdge to be processed
   * @param n the current depth or level of processing
   */
  public void addWikiDataEdgeDetails(Edge newEdge, int currentDepth) {
    Integer nextDepth = currentDepth + 1;
    processPropertyQueue(newEdge.propertyQID(), getAllValuesCurrentlyQueuedByN(currentDepth),
        nextDepth);
    processStringQueue(newEdge.label(), getAllValuesCurrentlyQueuedByN(currentDepth), nextDepth);
    processEntityQueue(newEdge.tgtVertexID(), getAllValuesCurrentlyQueuedByN(currentDepth),
        nextDepth);
  }

  /**
   * Checks if there are any items at a specific depth across all queues.
   * 
   * @param depth the depth to check
   * @return true if any queue contains items at the specified depth, false otherwise
   */
  public boolean hasItems(int depth) {
    return entityQueue.stream().anyMatch(entQ -> entQ.depth() == depth)
        || propertyQueue.stream().anyMatch(propQ -> propQ.depth() == depth)
        || stringQueue.stream().anyMatch(strQ -> strQ.depth() == depth);
  }

  /**
   * Marks an item as successfully fetched and removes it from the queues.
   * 
   * @param val the string used to the fetched item
   */
  public void fetchSuccessful(String val) {
    entityQueue.removeIf(entQ -> entQ.QID().equals(val));
    propertyQueue.removeIf(propQ -> propQ.QID().equals(val));
    stringQueue.removeIf(strQ -> strQ.value().equals(val));
    fetchedValues.add(new FetchedTarget(val));
  }

  /**
   * Adds a target value which could not be fetched to the deadValues list, removing it from the queues.
   */
  public void fetchUnsuccessful(String val) {
    entityQueue.removeIf(entQ -> entQ.QID().equals(val));
    propertyQueue.removeIf(propQ -> propQ.QID().equals(val));
    stringQueue.removeIf(strQ -> strQ.value().equals(val));
    deadValues.add(new DeadTarget(val));
  }

  /**
   * Retrieves a list of property QIDs at a specified depth, limited to the first 50 entries.
   * 
   * @param depth the depth of the items to retrieve
   * @return a list of property QIDs
   */
  public List<String> getPropertyQueue(int depth) {
    return propertyQueue.stream().filter(propQ -> propQ.depth() == depth).map(PropTarget::QID)
        .limit(50).toList();
  }

  /**
   * Retrieves a list of entity QIDs at a specified depth, limited to the first 50 entries.
   * 
   * @param depth the depth of the items to retrieve
   * @return a list of entity QIDs
   */
  public List<String> getEntityQueue(int depth) {
    return entityQueue.stream().filter(enttQ -> enttQ.depth() == depth).map(EntTarget::QID)
        .limit(50).toList();
  }

  /**
   * Retrieves a list of string values at a specified depth, limited to the first 50 entries.
   * 
   * @param depth the depth of the items to retrieve
   * @return a list of string values
   */
  public List<String> getStringQueue(int depth) {
    return stringQueue.stream().filter(strQ -> strQ.depth() == depth).map(StrTarget::value)
        .limit(50).toList();
  }

  /**
   * Provides a count of all queued values across all types.
   * 
   * @return a formatted string representing the counts of each queue
   */
  public String countALLQueuedValues() {
    return "EntityQueue: " + entityQueue.size() + " | PropertyQueue: " + propertyQueue.size()
        + " | StringQueue: " + stringQueue.size();
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  private List<String> getAllValuesCurrentlyQueuedByN(int n) {
    List<String> allValues = new ArrayList<>();

    for (EntTarget entQ : entityQueue) {
      if (entQ.depth() == n) {
        allValues.add(entQ.QID());
      }
    }

    for (PropTarget propQ : propertyQueue) {
      if (propQ.depth() == n) {
        allValues.add(propQ.QID());
      }
    }

    for (StrTarget strQ : stringQueue) {
      if (strQ.depth() == n) {
        allValues.add(strQ.value());
      }
    }

    for (FetchedTarget val : fetchedValues) {
      allValues.add(val.value());
    }

    return allValues;
  }

  private void processPropertyQueue(String propQID, List<String> currentQueue, int depth) {
    if (propQID != null && !currentQueue.contains(propQID)) {
      propertyQueue.add(new PropTarget(propQID, depth));
    }
  }

  private void processStringQueue(String value, List<String> currentQueue, int depth) {
    if (value != null && !currentQueue.contains(value)) {
      stringQueue.add(new StrTarget(value, depth));
    }
  }

  private void processEntityQueue(String tgtQID, List<String> currentQueue, int depth) {
    if (tgtQID != null && !currentQueue.contains(tgtQID)) {
      entityQueue.add(new EntTarget(tgtQID, depth));
    }
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //* RECORDS || RECORDS || RECORDS || RECORDS || RECORDS || RECORDS || RECORDS || RECORDS || RECORDS || RECORDS
  //
  //------------------------------------------------------------------------------------------------------------

  private record EntTarget(String QID, Integer depth) {
  }

  private record PropTarget(String QID, Integer depth) {
  }

  private record StrTarget(String value, Integer depth) {
  }

  private record FetchedTarget(String value) {
  }

  private record DeadTarget(String value) {
  }
}
