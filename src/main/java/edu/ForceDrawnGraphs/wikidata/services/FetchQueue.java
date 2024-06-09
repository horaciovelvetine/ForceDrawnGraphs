package edu.ForceDrawnGraphs.wikidata.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataEdge;

/**
 * Line at the DMV but for QID strings retrieved during the ingest process.
 */
public class FetchQueue implements Reportable {
  private Set<StrTarget> stringQueue;
  private Set<EntTarget> entityQueue;
  private Set<PropTarget> propertyQueue;
  private Set<FetchedTarget> fetchedValues;

  public FetchQueue() {
    this.entityQueue = new HashSet<>();
    this.stringQueue = new HashSet<>();
    this.propertyQueue = new HashSet<>();
    this.fetchedValues = new HashSet<>();
  }

  public void addWikiDataEdgeDetails(WikiDataEdge newEdge, int n) {
    // should check the entityQueue for each of 3 possible new targets on edge
    // should add value to the stringQueue to handle later 
    String newPropQID = newEdge.propertyQID();
    String strValue = newEdge.value();
    String newTgtQID = newEdge.tgtVertexQID();
    List<String> allValsCurrentlyInQ = getAllValuesCurrentlyQueuedByN(n);
    Integer nPlus = (n + 1);

    if (newPropQID != null && (!allValsCurrentlyInQ.contains(newPropQID))) {
      propertyQueue.add(new PropTarget(newPropQID, nPlus));
    }

    if (strValue != null && (!allValsCurrentlyInQ.contains(strValue))) {
      stringQueue.add(new StrTarget(strValue, nPlus));
    }

    if (newTgtQID != null && (!allValsCurrentlyInQ.contains(newTgtQID))) {
      entityQueue.add(new EntTarget(newTgtQID, nPlus));
    }

  }

  public boolean hasItems(int depth) {
    if (entityQueue.stream().anyMatch(entQ -> entQ.depth() == depth)) {
      return true;
    }

    if (propertyQueue.stream().anyMatch(propQ -> propQ.depth() == depth)) {
      return true;
    }
    //TODO Adjust to check string values as fetch is implemented
    // if (stringQueue.stream().anyMatch(strQ -> strQ.depth() == depth)) {
    //   return true;
    // }

    return false;
  }

  public void fetchSuccessful(String id) {
    if (entityQueue.stream().anyMatch(entQ -> entQ.QID().equals(id))) {
      entityQueue.removeIf(entQ -> entQ.QID().equals(id));
    }

    if (propertyQueue.stream().anyMatch(propQ -> propQ.QID().equals(id))) {
      propertyQueue.removeIf(propQ -> propQ.QID().equals(id));
    }

    if (stringQueue.stream().anyMatch(strQ -> strQ.value().equals(id))) {
      stringQueue.removeIf(strQ -> strQ.value().equals(id));
    }

    fetchedValues.add(new FetchedTarget(id));
  }


  public List<String> getPropertyQueue(int depth) {
    return propertyQueue.stream().filter(propQ -> propQ.depth() == depth).map(PropTarget::QID)
        .toList();
  }

  public List<String> getEntityQueue(int depth) {
    return entityQueue.stream().filter(enttQ -> enttQ.depth() == depth).map(EntTarget::QID)
        .toList();
  }

  public List<String> getStringQueue(int depth) {
    return stringQueue.stream().filter(strQ -> strQ.depth() == depth).map(StrTarget::value)
        .toList();
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

  //------------------------------------------------------------------------------------------------------------
  //
  //* RECORDS //||\\ RECORDS \\||// RECORDS //||\\ RECORDS \\||// RECORDS //||\\ RECORDS \\||// RECORDS
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
}
