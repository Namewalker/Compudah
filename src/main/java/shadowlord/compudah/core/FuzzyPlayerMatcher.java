package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FuzzyPlayerMatcher {

    private final double minimumConfidence;

    public FuzzyPlayerMatcher(double minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public MatchResult match(String query, Collection<String> candidates) {
        if (query == null || query.trim().isEmpty() || candidates == null || candidates.isEmpty()) {
            return new MatchResult(null, 0.0d, "none");
        }

        String normalizedQuery = normalize(query);
        List<MatchResult> scoredCandidates = new ArrayList<MatchResult>();

        for (String candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            String normalizedCandidate = normalize(candidate);
            if (normalizedCandidate.isEmpty()) {
                continue;
            }

            double score = scoreCandidate(normalizedQuery, normalizedCandidate);
            scoredCandidates.add(new MatchResult(candidate, score, "heuristic"));
        }

        if (scoredCandidates.isEmpty()) {
            return new MatchResult(null, 0.0d, "none");
        }

        Collections.sort(scoredCandidates, new Comparator<MatchResult>() {
            @Override
            public int compare(MatchResult left, MatchResult right) {
                return Double.compare(right.getConfidence(), left.getConfidence());
            }
        });

        MatchResult best = scoredCandidates.get(0);
        if (best.getConfidence() < minimumConfidence) {
            return new MatchResult(null, best.getConfidence(), best.getMethod());
        }
        return best;
    }

    private double scoreCandidate(String query, String candidate) {
        if (query.equals(candidate)) {
            return 1.0d;
        }

        if (candidate.startsWith(query)) {
            return 0.92d;
        }

        if (candidate.contains(query)) {
            return 0.84d;
        }

        if (query.length() == 0) {
            return 0.0d;
        }

        double levenshteinScore = computeLevenshteinScore(query, candidate);
        double lengthPenalty = 1.0d - Math.min(0.3d, Math.abs(query.length() - candidate.length()) / 10.0d);
        return Math.max(0.0d, Math.min(1.0d, levenshteinScore * 0.75d + lengthPenalty * 0.25d));
    }

    private double computeLevenshteinScore(String first, String second) {
        int[][] distances = new int[first.length() + 1][second.length() + 1];
        for (int i = 0; i <= first.length(); i++) {
            distances[i][0] = i;
        }
        for (int j = 0; j <= second.length(); j++) {
            distances[0][j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                int cost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                distances[i][j] = Math.min(Math.min(distances[i - 1][j] + 1, distances[i][j - 1] + 1),
                        distances[i - 1][j - 1] + cost);
            }
        }

        int maxLength = Math.max(first.length(), second.length());
        if (maxLength == 0) {
            return 1.0d;
        }
        return 1.0d - (distances[first.length()][second.length()] / (double) maxLength);
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().replaceAll("[^a-z0-9_]", "").trim();
    }

    public static final class MatchResult {
        private final String matchedName;
        private final double confidence;
        private final String method;

        public MatchResult(String matchedName, double confidence, String method) {
            this.matchedName = matchedName;
            this.confidence = confidence;
            this.method = method;
        }

        public String getMatchedName() {
            return matchedName;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getMethod() {
            return method;
        }
    }
}
