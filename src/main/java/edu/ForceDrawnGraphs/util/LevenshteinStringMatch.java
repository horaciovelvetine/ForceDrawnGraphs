package edu.ForceDrawnGraphs.util;

// Shout out to Baeldung for the primer on Fuzzy Matching Strings
// https://www.baeldung.com/cs/fuzzy-search-algorithm

/**
 * Provides a way to compute the Levenshtein distance (number of edit ops required to transform one
 * string into another), between two given strings.
 */
public class LevenshteinStringMatch {
  /**
   * Lev distance, full matrix computation, no threshold.
   */
  public static int computeLevenshteinDistance(String str1, String str2) {
    int[][] dp = new int[str1.length() + 1][str2.length() + 1]; // 2D array sized by strings + 1 to map the distances

    for (int i = 0; i <= str1.length(); i++) { // iterate through the first string
      for (int j = 0; j <= str2.length(); j++) { // iterate through the second string
        if (i == 0) { // if first string is empty, distance is the length of the second string
          dp[i][j] = j;
        } else if (j == 0) { // if second string is empty, distance is the length of the first string
          dp[i][j] = i;
        } else { // if neither string is empty, find LEV distance
          dp[i][j] = Math.min(dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1),
              Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
        }
      }
    }

    return dp[str1.length()][str2.length()];
  }

  /**
   * Lev distance, optimized for approximate matching by implementing a threshold and only storing the previous and current rows of text from the strings.
   */
  public static int computeLevApproxWithThreshold(String str1, String str2, int threshold) {
    int len1 = str1.length();
    int len2 = str2.length();
    int[] prev = new int[len2 + 1];
    int[] curr = new int[len2 + 1];

    for (int j = 0; j <= len2; j++) {
      prev[j] = j;
    }

    for (int i = 1; i <= len1; i++) {
      curr[0] = i;
      int minDistance = curr[0]; // Initialize the minDist found in the current row

      for (int j = 1; j <= len2; j++) {
        int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
        curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);

        if (curr[j] < minDistance) { // update minDist for the current row
          minDistance = curr[j];
        }
      }

      if (minDistance > threshold) { //exit if minDist is greater than threshold
        return threshold + 1;
      }

      // swap rows and iterate for iteration
      int[] temp = prev;
      prev = curr;
      curr = temp;
    }

    return prev[len2];
  }
}
