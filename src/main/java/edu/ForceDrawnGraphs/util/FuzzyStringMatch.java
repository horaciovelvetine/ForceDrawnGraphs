package edu.ForceDrawnGraphs.util;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.ForceDrawnGraphs.models.Vertex;

public class FuzzyStringMatch {
  public static Integer MATCH_THRESHOLD = 11;

  /**
   * Fuzzy matches a target string to a list of vertices.
   * 
   * @param target   The target string to match.
   * @param vertices The list of vertices to match against.
   * @return A list of vertices that match the target string.
   */
  public static List<Vertex> fuzzyMatch(String target, List<Vertex> vertices) {
    List<VertexDistance> distances = new ArrayList<>();
    for (Vertex vertex : vertices) {
      int distance = LevenshteinStringMatch.computeLevenshteinDistance(target, vertex.label());
      distances.add(new VertexDistance(vertex, distance));
    }

    Collections.sort(distances, Comparator.comparingInt(VertexDistance::getDistance));

    List<Vertex> result = new ArrayList<>();
    for (VertexDistance vertexDistance : distances) {
      if (vertexDistance.getDistance() < MATCH_THRESHOLD) {
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

  public static List<Vertex> fuzzyMatch(String label, Set<Vertex> vertices) {
    return fuzzyMatch(label, new ArrayList<>(vertices));
  }
}
