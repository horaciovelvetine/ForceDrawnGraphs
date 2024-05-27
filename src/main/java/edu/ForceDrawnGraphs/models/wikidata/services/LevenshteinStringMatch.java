package edu.ForceDrawnGraphs.models.wikidata.services;

// Shout out to Baeldung for the primer on Fuzzy Matching Strings
// https://www.baeldung.com/cs/fuzzy-search-algorithm

public class LevenshteinStringMatch {
  public static int computeLevenshteinDistance(String str1, String str2) {
    int[][] dp = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      for (int j = 0; j <= str2.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          dp[i][j] = Math.min(dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1),
              Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
        }
      }
    }

    return dp[str1.length()][str2.length()];
  }
}
