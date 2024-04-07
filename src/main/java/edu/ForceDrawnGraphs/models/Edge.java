package edu.ForceDrawnGraphs.models;

public class Edge {
  private int id;
  private int srcVertexId; // actual serial ID
  private int tgtVertextId; // actual serial ID
  private int weight;
  private String edgeTypeId; // The corresponding property_id;
  private String edgeType; // Intended to be the en_label of the corresponding property;
}
