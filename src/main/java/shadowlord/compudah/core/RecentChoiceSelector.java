package shadowlord.compudah.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Selects from a pool while temporarily avoiding recently selected values. */
public class RecentChoiceSelector {

    private static final int MAX_HISTORY_KEYS = 2048;

    private final int historySize;
    private final LinkedHashMap<String, Deque<String>> histories = new LinkedHashMap<String, Deque<String>>(16, 0.75F, true);

    public RecentChoiceSelector(int historySize) {
        this.historySize = Math.max(0, historySize);
    }

    public synchronized String choose(String key, List<String> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }

        String historyKey = key == null ? "default" : key;
        Deque<String> recent = histories.get(historyKey);
        List<String> available = new ArrayList<String>();
        for (String option : options) {
            if (option != null && !option.trim().isEmpty() && (recent == null || !recent.contains(option))) {
                available.add(option);
            }
        }
        if (available.isEmpty()) {
            for (String option : options) {
                if (option != null && !option.trim().isEmpty()) {
                    available.add(option);
                }
            }
        }
        if (available.isEmpty()) {
            return null;
        }

        String selected = available.get(ThreadLocalRandom.current().nextInt(available.size()));
        if (historySize > 0) {
            if (recent == null) {
                recent = new ArrayDeque<String>();
                histories.put(historyKey, recent);
            }
            recent.remove(selected);
            recent.addFirst(selected);
            while (recent.size() > historySize) {
                recent.removeLast();
            }
            trimHistories();
        }
        return selected;
    }

    public synchronized void clearPrefix(String prefix) {
        if (prefix == null) {
            return;
        }
        for (String key : new ArrayList<String>(histories.keySet())) {
            if (key.startsWith(prefix)) {
                histories.remove(key);
            }
        }
    }

    private void trimHistories() {
        while (histories.size() > MAX_HISTORY_KEYS) {
            Map.Entry<String, Deque<String>> eldest = histories.entrySet().iterator().next();
            histories.remove(eldest.getKey());
        }
    }
}
