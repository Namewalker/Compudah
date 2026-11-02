package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class WakeWordDetectorTest {

    @Test
    void shouldRecognizeWakeWordWithPunctuationAndCaseVariance() {
        WakeWordDetector detector = new WakeWordDetector(Arrays.asList("compudah", "computer"));

        assertTrue(detector.containsWakeWord("Compudah kill op"));
        assertTrue(detector.containsWakeWord("COMPUDAH, kill op"));
        assertTrue(detector.containsWakeWord("hey computer please heal me"));
        assertTrue(detector.containsWakeWord("compudah! heal me"));
    }

    @Test
    void shouldIgnoreMessagesWithoutWakeWord() {
        WakeWordDetector detector = new WakeWordDetector(Collections.singletonList("compudah"));

        assertFalse(detector.containsWakeWord("please heal me"));
        assertFalse(detector.containsWakeWord("hello there friend"));
        assertNull(detector.extractCommandIfAddressed("hello there friend"));
    }
}
