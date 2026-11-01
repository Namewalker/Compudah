package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FuzzyPlayerMatcherTest {

    @Test
    void shouldFavorClosestMatchingPlayerName() {
        FuzzyPlayerMatcher matcher = new FuzzyPlayerMatcher(0.65d);

        FuzzyPlayerMatcher.MatchResult result = matcher.match("op", Arrays.asList("op_clutcher", "operator123", "ShadowLord"));

        assertEquals("op_clutcher", result.getMatchedName());
        assertTrue(result.getConfidence() > 0.0d);
    }

    @Test
    void shouldHandleExactNameMatch() {
        FuzzyPlayerMatcher matcher = new FuzzyPlayerMatcher(0.65d);

        FuzzyPlayerMatcher.MatchResult result = matcher.match("ShadowLord", Arrays.asList("ShadowLord", "operator123"));

        assertEquals("ShadowLord", result.getMatchedName());
        assertTrue(result.getConfidence() >= 1.0d);
    }
}
