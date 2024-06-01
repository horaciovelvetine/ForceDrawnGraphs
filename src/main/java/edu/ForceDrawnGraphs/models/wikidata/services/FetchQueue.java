package edu.ForceDrawnGraphs.models.wikidata.services;

import java.util.List;
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

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* ADD & CHECK VARIOUS TYPES OF ITEMS - ADD & CHECK VARIOUS TYPES OF ITEMS - ADD & CHECK VARIOUS TYPES OF ITEMS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  public void addStringToQueue(String string) {
    stringQueue.add(new StringTarget(string));
  }

  public boolean queueContainsString(String string) {
    return stringQueue.stream().anyMatch(item -> item.string.equals(string));
  }

  public void addEntityToQueue(String QID) {
    entityQueue.add(new EntityTarget(QID));
  }

  public boolean queueContainsEntity(String QID) {
    return entityQueue.stream().anyMatch(item -> item.QID.equals(QID));
  }

  public void addPropertyToQueue(String QID) {
    propertyQueue.add(new PropertyTarget(QID));
  }

  public boolean queueContainsProperty(String QID) {
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
