package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WakeWordDetector {

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
        String normalized = normalize(message);
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
        String normalized = normalize(message);
        String wakeWord = extractWakeWord(message);
        if (wakeWord == null || wakeWord.isEmpty()) {
            return normalized;
        }

        int index = normalized.indexOf(wakeWord);
        if (index < 0) {
            return normalized;
        }
        int commandStart = index + wakeWord.length();
        if (commandStart >= normalized.length()) {
            return "";
        }
        String command = normalized.substring(commandStart).trim();
        return command.replaceAll("\\s+", " ").trim();
    }

    public String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.toLowerCase();
        normalized = normalized.replaceAll("[.,!?;:]+", " ");
        normalized = normalized.replaceAll("[^a-z0-9\\s]", " ");
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }

    public String normalizeWord(String value) {
        if (value == null) {
            return "";
        }
        return normalize(value);
    }
}
