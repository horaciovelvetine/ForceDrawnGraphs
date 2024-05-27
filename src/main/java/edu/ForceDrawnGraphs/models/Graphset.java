package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;
import java.util.List;

import edu.ForceDrawnGraphs.models.wikidata.services.FetchQueue;
import edu.ForceDrawnGraphs.models.wikidata.services.FuzzyStringMatch;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
public class Graphset {
  private String originQuery; // can be used to find the origin a little bit later...
  private ArrayList<Property> properties; // LocalStore for any properties that are fetched from the API
  private ArrayList<Edge> edges; // In theory these may become a network as the Guava library is integrated
  private ArrayList<Vertex> vertices; // ditto
  private FetchQueue fetchQueue;

  public Graphset() {
    this.vertices = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.properties = new ArrayList<>();
    this.fetchQueue = new FetchQueue();
  }

  public void setOriginQuery(String originQuery) {
    this.originQuery = originQuery;
  }

  public String originQuery() {
    return originQuery;
  }

  public ArrayList<Vertex> vertices() {
    return vertices;
  }

  public ArrayList<Edge> edges() {
    return edges;
  }

  public FetchQueue fetchQueue() {
    return fetchQueue;
  }
  //! ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS

  public void addVertex(Vertex vertex) {
    if (!vertices.contains(vertex))
      vertices.add(vertex);
  }

  public void addEdge(Edge edge) {
    if (edges.contains(edge))
      return;

    // New Edge found - add it to the Graphset
    // check for deliquent ent values which are not in the Graphset
    // add them to the fetchQueue
    edges.add(edge);
    checkForUnfetchedEntityDetails(edge);

  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  private void checkForUnfetchedEntityDetails(Edge edge) {
    String tgtQID = edge.tgtVertexQID();
    String propQID = edge.details().propQID();
    String valueTgt = edge.details().valueTgt();

    if (tgtQID != null && tgtQIDNotInGraphset(tgtQID) && queryNotInFetchQueue(tgtQID)) {
      // Add a target entity QID to the fetchQueue
      fetchQueue.addEntityQIDToQueue(tgtQID);
    }

    if (propQID != null && propQIDNotInGraphset(propQID) && queryNotInFetchQueue(propQID)) {
      // Add a property QID to the fetchQueue
      fetchQueue.addEntityQIDToQueue(propQID);
    }

    if (valueTgt != null && noVertexFuzzyMatchesValueTgt(valueTgt) && queryNotInFetchQueue(valueTgt)) {
      // Add a value target to the fetchQueue (typically a date)
      fetchQueue.addEntityQIDToQueue(valueTgt);
    }
  }

  private boolean tgtQIDNotInGraphset(String tgtQID) {
    // noneMatch reflexts the (not) in the method name
    return vertices.stream().noneMatch(v -> v.details().QID().equals(tgtQID));
  }

  private boolean propQIDNotInGraphset(String propQID) {
    // noneMatch reflexts the (not) in the method name
    return properties.stream().noneMatch(p -> p.QID().equals(propQID));
  }

  private boolean queryNotInFetchQueue(String query) {
    // the !(not) here is to match the method name
    return !fetchQueue.queueContainsQuery(query);
  }

  private boolean noVertexFuzzyMatchesValueTgt(String valueTgt) {
    // match sensitivity can be adjust in the FuzzyStringMatch class
    return FuzzyStringMatch.fuzzyMatch(valueTgt, vertices).isEmpty();
  }
}
