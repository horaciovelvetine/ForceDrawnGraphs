package edu.ForceDrawnGraphs.models;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import edu.ForceDrawnGraphs.interfaces.Reportable;
import edu.ForceDrawnGraphs.util.FuzzyStringMatch;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataEdge;
import edu.ForceDrawnGraphs.wikidata.models.WikiDataVertex;
import edu.ForceDrawnGraphs.wikidata.services.FetchQueue;
import edu.ForceDrawnGraphs.wikidata.services.StmtProc;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Central class for storing something akin to 'state' for the initial request creating a session/universe/TBD. 
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Graphset implements Reportable {
  private int N;
  private String originQuery;
  private FetchQueue wikiDataFetchQueue;
  // STORE
  private Set<Property> properties;
  private Set<WikiDataEdge> edges;
  private Set<WikiDataVertex> vertices;

  public Graphset() {
    N = 0; // set-depth as distance in number of edges from origin
    this.vertices = ConcurrentHashMap.newKeySet();
    this.edges = ConcurrentHashMap.newKeySet();
    this.properties = ConcurrentHashMap.newKeySet();
    this.wikiDataFetchQueue = new FetchQueue();
  }

  public void setOriginQuery(String originQuery) {
    this.originQuery = originQuery;
  }

  public String originQuery() {
    return originQuery;
  }

  public Set<WikiDataVertex> vertices() {
    return vertices;
  }

  public Set<WikiDataEdge> edges() {
    return edges;
  }

  public FetchQueue wikiDataFetchQueue() {
    return wikiDataFetchQueue;
  }

  public void iterateNDepth() {
    N++;
  }

  public int depth() {
    return N;
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //
  //* PUBLIC ADD METHODS - PUBLIC ADD METHODS - PUBLIC ADD METHODS - PUBLIC ADD METHODS - PUBLIC ADD METHODS
  //
  //
  //------------------------------------------------------------------------------------------------------------
  /**
   * Adds a vertex to the Graphset storage.
   * 
   * @param vertex Vertex to add to the Graphset storage.
   * 
   */
  public void addVertexToLookup(Vertex vertex) {
    if (vertexDetailsAlreadyPresent(vertex.id()))
      return;
    if (vertex instanceof WikiDataVertex)
      vertices.add((WikiDataVertex) vertex);
  }

  /**
   * Adds a property to the Graphset storage, and updates the appropriate vertex with the matching property QID.
   * 
   * @param property Property to add to the Graphset storage.
   * 
   */
  public void addPropToLookup(Property property) {
    propertyLabelMatchesExistingVertex(property);

    if (propertyDetailsAlreadyPresent(property))
      return;

    properties.add(property);
  }

  /**
   * Adds a list of edges to the Graphset storage and updates the appropriate fetchQueue with details from the edges.
   * 
   * @param newEdges List of edges to add to the Graphset storage.
   * 
   */
  public void addEdgesToLookupAndQueue(StmtProc stmt) {
    Edge edge = stmt.msEdge();

    if (edge instanceof WikiDataEdge) {
      wikiDataFetchQueue.addWikiDataEdgeDetails((WikiDataEdge) edge, N);
      edges.add((WikiDataEdge) edge);
    }
  }

  /**
   * Checks all of the edges in the Graphset for a match with the query value, and assigns the dateVertex(QID) to the edge.
   * 
   * @param dateVertex The newley created dateVertex to assign to the edges.
   * @param queryVal The query value used to store and query the WD API (will be equal to value on edge if matches).
   */
  public void assignDateVertexToEdges(WikiDataVertex dateVertex, String queryVal) {
    for (Edge edge : edges) {
      if (isValidWikiDataEdge(edge, queryVal)) {
        ((WikiDataEdge) edge).setTgtVertexID(dateVertex.QID());
      }
    }
  }

  /**
   * Gets all Edges of the graphset where the source & target vertices entity data has been fetched.
   */
  public Set<Edge> getCompleteWikidataEnts() {
    return edges.stream().filter(edge -> edge instanceof WikiDataEdge)
        .map(edge -> (WikiDataEdge) edge).filter(wikiEdge -> vertexExists(wikiEdge.srcVertexID())
            && vertexExists(wikiEdge.tgtVertexID()))
        .collect(Collectors.toSet());
  }

  /**
   * Gets the associated vertices of an edge.
   */
  public Optional<Pair<Vertex>> getAssociatedVertices(Edge edge) {
    if (edge instanceof WikiDataEdge) {
      WikiDataEdge wikiEdge = (WikiDataEdge) edge;
      Vertex srcVertex = findVertexById(wikiEdge.srcVertexID());
      Vertex tgtVertex = findVertexById(wikiEdge.tgtVertexID());
      if (srcVertex != null && tgtVertex != null) {
        return Optional.of(new Pair<>(srcVertex, tgtVertex));
      }
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    return "Graphset [N=" + N + ", originQuery=" + originQuery + ", properties=" + properties.size()
        + ", edges=" + edges.size() + ", vertices=" + vertices.size() + ", wikiDataFetchQueue="
        + wikiDataFetchQueue.countALLQueuedValues() + "]";
  }

  //------------------------------------------------------------------------------------------------------------
  //
  //! PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS - PRIVATE METHODS
  //
  //------------------------------------------------------------------------------------------------------------

  private boolean isValidWikiDataEdge(Edge edge, String queryVal) {
    if (!(edge instanceof WikiDataEdge)) {
      return false;
    }
    WikiDataEdge wikiEdge = (WikiDataEdge) edge;
    return wikiEdge.value() != null && wikiEdge.value().equals(queryVal);
  }

  private boolean vertexDetailsAlreadyPresent(String newVertexID) {
    for (Vertex vertex : vertices) {
      if (vertex.id().equals(newVertexID)) {
        return true;
      }
    }
    return false;
  }

  private boolean propertyDetailsAlreadyPresent(Property newProperty) {
    for (Property property : properties) {
      if (property.ID().equals(newProperty.ID())) {
        return true;
      }
    }
    return false;
  }

  private boolean vertexExists(String vertexQID) {
    return vertices.stream().anyMatch(vertex -> vertex instanceof WikiDataVertex
        && ((WikiDataVertex) vertex).QID().equals(vertexQID));
  }

  private Vertex findVertexById(String vertexId) {
    return vertices.stream()
        .filter(v -> v instanceof WikiDataVertex && ((WikiDataVertex) v).QID().equals(vertexId))
        .findFirst().orElse(null);
  }

  private void propertyLabelMatchesExistingVertex(Property property) {
    List<Vertex> matchedVertices = FuzzyStringMatch.fuzzyMatch(property.label(), vertices, 2);
    if (matchedVertices.size() == 1) {
      WikiDataVertex vert = (WikiDataVertex) matchedVertices.get(0);
      vert.setMatchingPropertyQID(property.ID());
    } else if (matchedVertices.size() > 1) {
      report("Multiple vertices matched for property: " + property.ID());
    }
  }

}
