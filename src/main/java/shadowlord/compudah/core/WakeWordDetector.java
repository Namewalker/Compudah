package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class WakeWordDetector {

    private static final Pattern PUNCTUATION = Pattern.compile("[.,!?;:]+");
    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9\\s]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final List<String> wakeWords;

    public WakeWordDetector(Collection<String> customWakeWords) {
        this.wakeWords = new ArrayList<String>();
        if (customWakeWords != null) {
            for (String candidate : customWakeWords) {
                if (candidate != null) {
                    String normalized = normalizeWord(candidate);
                    if (!normalized.isEmpty() && !this.wakeWords.contains(normalized)) {
                        this.wakeWords.add(normalized);
                    }
                }
            }
        }
        if (this.wakeWords.isEmpty()) {
            this.wakeWords.add("compudah");
            this.wakeWords.add("computer");
        }
    }

    public boolean containsWakeWord(String message) {
        return extractWakeWord(message) != null;
    }

    public String extractWakeWord(String message) {
        if (message == null) {
            return null;
        }
        return findWakeWord(normalize(message));
    }

    private String findWakeWord(String normalized) {
        for (String wakeWord : wakeWords) {
            if (normalized.equals(wakeWord) || normalized.startsWith(wakeWord + " ")
                    || normalized.contains(" " + wakeWord + " ") || normalized.endsWith(" " + wakeWord)) {
                return wakeWord;
            }
        }
        return null;
    }

    public String extractCommand(String message) {
        if (message == null) {
            return "";
        }
        String addressedCommand = extractCommandIfAddressed(message);
        return addressedCommand == null ? normalize(message) : addressedCommand;
    }

    public String extractCommandIfAddressed(String message) {
        if (message == null) {
            return null;
        }
        String normalized = normalize(message);
        String wakeWord = findWakeWord(normalized);
        if (wakeWord == null || wakeWord.isEmpty()) {
            return null;
        }

        int index = normalized.indexOf(wakeWord);
        if (index < 0) {
            return normalized;
        }
        int commandStart = index + wakeWord.length();
        if (commandStart >= normalized.length()) {
            return "";
        }
        return normalized.substring(commandStart).trim();
    }

    public String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.toLowerCase(Locale.ENGLISH);
        normalized = PUNCTUATION.matcher(normalized).replaceAll(" ");
        normalized = NON_WORD.matcher(normalized).replaceAll(" ");
        normalized = WHITESPACE.matcher(normalized).replaceAll(" ").trim();
        return normalized;
    }

    public String normalizeWord(String value) {
        if (value == null) {
            return "";
        }
        return normalize(value);
    }
}
