package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import shadowlord.compudah.api.Intent;

public class IntentRegistry {

    private final Map<String, Intent> intents = new ConcurrentHashMap<String, Intent>();

    public synchronized void register(Intent intent) {
        if (intent == null) {
            return;
        }
        for (String alias : intent.getAliases()) {
            intents.put(alias.toLowerCase(), intent);
        }
    }

    public Intent resolve(String action) {
        if (action == null || action.trim().isEmpty()) {
            return null;
        }
        return intents.get(action.trim().toLowerCase());
    }

    public synchronized Resolution resolveCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return new Resolution(null, new String[0]);
        }

        String normalized = command.trim().toLowerCase();
        String[] words = normalized.split("\\s+");
        StringBuilder alias = new StringBuilder();
        Intent bestIntent = null;
        int bestWordCount = 0;
        for (int index = 0; index < words.length; index++) {
            if (index > 0) {
                alias.append(' ');
            }
            alias.append(words[index]);
            Intent candidate = intents.get(alias.toString());
            if (candidate != null) {
                bestIntent = candidate;
                bestWordCount = index + 1;
            }
        }
        if (bestIntent != null) {
            String[] arguments = new String[words.length - bestWordCount];
            System.arraycopy(words, bestWordCount, arguments, 0, arguments.length);
            return new Resolution(bestIntent, arguments);
        }
        return new Resolution(null, words);
    }

    public synchronized List<Intent> getIntents() {
        List<Intent> result = new ArrayList<Intent>();
        for (Intent intent : intents.values()) {
            if (!result.contains(intent)) {
                result.add(intent);
            }
        }
        return result;
    }

    public static final class Resolution {
        private final Intent intent;
        private final String[] arguments;

        private Resolution(Intent intent, String[] arguments) {
            this.intent = intent;
            this.arguments = arguments;
        }

        public Intent getIntent() {
            return intent;
        }

        public String[] getArguments() {
            return arguments.clone();
        }
    }
}
