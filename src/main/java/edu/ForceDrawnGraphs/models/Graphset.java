package edu.ForceDrawnGraphs.models;

import java.util.ArrayList;
import java.util.List;

import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiMainSnakEdge;
import edu.ForceDrawnGraphs.models.wikidata.models.WikiQualifierEdge;
import edu.ForceDrawnGraphs.models.wikidata.services.FetchQueue;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
public class Graphset implements Reportable {
  private String originQuery; // can be used to find the origin a little bit later...
  // private ArrayList<Property> properties; // LocalStore for any properties that are fetched from the API
  private ArrayList<Edge> edges; // In theory these may become a network as the Guava library is integrated
  private ArrayList<Vertex> vertices; // ditto
  private FetchQueue fetchQueue;

  public Graphset() {
    this.vertices = new ArrayList<>();
    this.edges = new ArrayList<>();
    // this.properties = new ArrayList<>();
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

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS - ADD METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

  public void addVertex(Vertex vertex) {
    if (!vertices.contains(vertex))
      vertices.add(vertex);
  }

  public void addEdgesAndUpdateFetchQueue(List<Edge> newEdges) {
    for (Edge edge : newEdges) {
      // add edge to dataset
      edges.add(edge);
      
      if (edge instanceof WikiMainSnakEdge) {
        WikiMainSnakEdge mainSnakEdge = (WikiMainSnakEdge) edge;

        String propertyQIDToCheck = mainSnakEdge.propertyQID();
        if (!fetchQueue.queueContainsString(propertyQIDToCheck))
          fetchQueue.addStringToQueue(propertyQIDToCheck);

        String tgtVertexQID = mainSnakEdge.tgtVertexQID();
        if (tgtVertexQID != null && !fetchQueue.queueContainsString(tgtVertexQID))
          fetchQueue.addStringToQueue(tgtVertexQID);

        String valueTarget = mainSnakEdge.value();
        if (valueTarget != null && !fetchQueue.queueContainsString(valueTarget))
          fetchQueue.addStringToQueue(valueTarget);

      } else if (edge instanceof WikiQualifierEdge) {
        WikiQualifierEdge qualifierEdge = (WikiQualifierEdge) edge;

        String propertyQIDToCheck = qualifierEdge.propertyQID();
        if (!fetchQueue.queueContainsString(propertyQIDToCheck))
          fetchQueue.addStringToQueue(propertyQIDToCheck);

        String tgtVertexQID = qualifierEdge.tgtVertexQID();
        if (tgtVertexQID != null && !fetchQueue.queueContainsString(tgtVertexQID))
          fetchQueue.addStringToQueue(tgtVertexQID);

        String valueTarget = qualifierEdge.value();
        if (valueTarget != null && !fetchQueue.queueContainsString(valueTarget))
          fetchQueue.addStringToQueue(valueTarget);

      }
      // TODO WILL STOP HERE (START HERE NEXT TIME)
      // check edge for propertyQID value and add to fetchQueue if not already present
      // check edge for tgtVertexQID value and add to fetchQueue if not already present
      // refactor the WikiEdges to WikiDataEdge, and add another source Enum (to prevent the above)
      // refactor to send appropariate ents to the appropriate queues (entity, property, string)
      print("Adding edge to dataset: " + edge.srcVertexQID() + " -> " + edge.tgtVertexQID());
    }
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //! PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS // PRIVATE METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------

}
