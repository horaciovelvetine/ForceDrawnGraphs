package edu.ForceDrawnGraphs.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.ForceDrawnGraphs.models.Vertex;

public class FuzzyStringMatch {
  public static List<Vertex> fuzzyMatch(String target, List<Vertex> vertices) {
    List<VertexDistance> distances = new ArrayList<>();
    for (Vertex vertex : vertices) {
      int distance = LevenshteinStringMatch.computeLevenshteinDistance(target, vertex.label());
      distances.add(new VertexDistance(vertex, distance));
    }

    Collections.sort(distances, Comparator.comparingInt(VertexDistance::getDistance));

    List<Vertex> result = new ArrayList<>();
    for (VertexDistance vertexDistance : distances) {
      // If the distance is less than 5, add the vertex to the result list
      // 5 is an arbitrary number, and a starting place for tuning the fuzzy matching
      if (vertexDistance.getDistance() < 5) {
        result.add(vertexDistance.getVertex());
      }
    }
    return result;
  }

  private static class VertexDistance {
    private Vertex vertex;
    private int distance;

    public VertexDistance(Vertex vertex, int distance) {
      this.vertex = vertex;
      this.distance = distance;
    }

    public Vertex getVertex() {
      return vertex;
    }

    public int getDistance() {
      return distance;
    }
  }
}
