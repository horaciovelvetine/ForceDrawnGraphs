package edu.ForceDrawnGraphs.util;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.ForceDrawnGraphs.models.Vertex;

public class FuzzyStringMatch {
  /**
   * Fuzzy matches a target string to a list of vertices.
   * 
   * @param target   The target string to match.
   * @param vertices The list of vertices to match against.
   * @param threshold An arbitrary threshold value to establish match strength (lower
   * values enforce more strict match results).
   * @return A list of vertices that match the target string.
   */
  public static List<Vertex> fuzzyMatch(String target, List<Vertex> vertices, Integer threshold) {
    List<VertexDistance> distances = new ArrayList<>();
    List<Vertex> directMatches = new ArrayList<>();
    for (Vertex vertex : vertices) {
      if (vertex.label() == null || target == null) {
        continue; // ignore null labels & targets 
      }

      if (vertex.label().contains(target)) { // substring match
        directMatches.add(vertex);
      } else {
        int distance = LevenshteinStringMatch.computeLevApproxWithThreshold(target, vertex.label(),
            (threshold * 2)); // mult by 2 to expand the threshold slightly for better results
        distances.add(new VertexDistance(vertex, distance));
      }
    }

    Collections.sort(distances, Comparator.comparingInt(VertexDistance::distance));

    List<Vertex> result = new ArrayList<>(directMatches); // substring matches first
    for (VertexDistance vertexDistance : distances) {
      if (vertexDistance.distance() < threshold) {
        result.add(vertexDistance.vertex());
      }
    }
    return result;
  }

  public static List<Vertex> fuzzyMatch(String label, Set<Vertex> vertices, Integer threshold) {
    return fuzzyMatch(label, new ArrayList<>(vertices), threshold);
  }

  private static record VertexDistance(Vertex vertex, int distance) {
  }
}
