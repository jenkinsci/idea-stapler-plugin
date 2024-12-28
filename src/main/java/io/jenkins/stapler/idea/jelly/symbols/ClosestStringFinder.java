package io.jenkins.stapler.idea.jelly.symbols;

import java.util.Set;

public final class ClosestStringFinder {

    /**
     * Finds the closest string in the set to the target string.
     *
     * @param target The string to compare against.
     * @param set The set of strings to search.
     * @return The closest string, or null if the set is empty.
     */
    public static String findClosestString(String target, Set<String> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }

        // Extract the portion before the first space in the target string
        String targetPrefix = target.split(" ")[0];

        String closestString = null;
        int smallestDistance = 5;

        for (String candidate : set) {
            // Extract the portion before the first space in the candidate string
            String candidatePrefix = candidate.split(" ")[0];

            // Calculate Levenshtein distance on the prefixes
            int distance = levenshteinDistance(targetPrefix, candidatePrefix);

            // Update the closest string if a smaller distance is found
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestString = candidate; // Store the full candidate string
            }
        }

        return closestString;
    }

    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
