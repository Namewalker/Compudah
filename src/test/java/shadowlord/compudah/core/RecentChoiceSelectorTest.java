package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class RecentChoiceSelectorTest {

    @Test
    void shouldAvoidTheMostRecentlySelectedVariantWhenAlternativesExist() {
        RecentChoiceSelector selector = new RecentChoiceSelector(2);
        List<String> variants = Arrays.asList("orbital-laser", "meteor-strike", "anvil-justice");

        String first = selector.choose("player:kill", variants);
        String second = selector.choose("player:kill", variants);

        assertNotEquals(first, second);
        assertTrue(variants.contains(first));
        assertTrue(variants.contains(second));
    }
}
