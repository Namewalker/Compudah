package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;
import shadowlord.compudah.api.IntentContext;
import shadowlord.compudah.api.IntentVariant;

/** Registers, selects, and runs per-intent behavior variants. */
public class VariantManager {

    private final Map<String, List<IntentVariant>> variants = new LinkedHashMap<String, List<IntentVariant>>();
    private final Map<String, IntentVariant> mostRecentByPlayerAndIntent = new ConcurrentHashMap<String, IntentVariant>();
    private final RecentChoiceSelector selector;
    private final ThreadLocal<Boolean> immediateExecution = new ThreadLocal<Boolean>();

    public VariantManager(int antiRepeatHistory) {
        this.selector = new RecentChoiceSelector(antiRepeatHistory);
    }

    /** Registers an extension-provided behavior variant. */
    public synchronized void registerVariant(String intentName, IntentVariant variant) {
        if (intentName == null || intentName.trim().isEmpty() || variant == null
                || variant.getId() == null || variant.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Intent name and variant id are required");
        }
        String key = intentName.toLowerCase();
        List<IntentVariant> registered = variants.get(key);
        if (registered == null) {
            registered = new ArrayList<IntentVariant>();
            variants.put(key, registered);
        }
        for (IntentVariant existing : registered) {
            if (existing.getId().equalsIgnoreCase(variant.getId())) {
                throw new IllegalArgumentException("Variant already registered: " + intentName + "/" + variant.getId());
            }
        }
        registered.add(variant);
    }

    /** Returns an immutable snapshot of variants registered for an intent. */
    public synchronized List<IntentVariant> getVariants(String intentName) {
        List<IntentVariant> registered = variants.get(intentName == null ? "" : intentName.toLowerCase());
        return registered == null ? Collections.<IntentVariant>emptyList()
                : Collections.unmodifiableList(new ArrayList<IntentVariant>(registered));
    }

    /** Chooses a variant without repeating recent choices for this player and intent. */
    public IntentVariant choose(Player player, String intentName) {
        return choose(player == null ? null : IntentContext.forInGame(player), intentName);
    }

    public IntentVariant choose(IntentContext context, String intentName) {
        List<IntentVariant> registered = getVariants(intentName);
        if (registered.isEmpty()) {
            return null;
        }
        List<String> identifiers = new ArrayList<String>(registered.size());
        Map<String, IntentVariant> byId = new LinkedHashMap<String, IntentVariant>();
        for (IntentVariant variant : registered) {
            identifiers.add(variant.getId());
            byId.put(variant.getId(), variant);
        }
        String historyKey = (context == null ? "server" : context.getUniqueId().toString()) + ":" + intentName.toLowerCase();
        String selectedId = selector.choose(historyKey, identifiers);
        IntentVariant selected = byId.get(selectedId);
        if (context != null && selected != null) {
            mostRecentByPlayerAndIntent.put(historyKey, selected);
        }
        return selected;
    }

    /** Runs the selected variant, falling back to the intent's ordinary behavior if none are registered. */
    public void execute(Player player, String[] arguments, CompudahPlugin plugin, Intent intent, boolean immediate) {
        execute(player == null ? null : IntentContext.forInGame(player), arguments, plugin, intent, immediate);
    }

    public void execute(IntentContext context, String[] arguments, CompudahPlugin plugin, Intent intent, boolean immediate) {
        if (intent == null) {
            return;
        }
        if (context == null) {
            return;
        }
        if (immediate) {
            immediateExecution.set(Boolean.TRUE);
            try {
                intent.execute(context, arguments, plugin);
            } finally {
                immediateExecution.remove();
            }
            return;
        }
        if (context.getPlayer() == null) {
            intent.execute(context, arguments, plugin);
            return;
        }
        IntentVariant selected = choose(context, intent.getName());
        if (selected == null) {
            intent.execute(context, arguments, plugin);
        } else {
            selected.execute(context, arguments, plugin, intent);
        }
    }

    public boolean isImmediateExecution() {
        return Boolean.TRUE.equals(immediateExecution.get());
    }

    public IntentVariant getMostRecent(Player player, String intentName) {
        return getMostRecent(player == null ? null : player.getUniqueId(), intentName);
    }

    public IntentVariant getMostRecent(IntentContext context, String intentName) {
        return getMostRecent(context == null ? null : context.getUniqueId(), intentName);
    }

    public IntentVariant getMostRecent(UUID playerId, String intentName) {
        if (playerId == null || intentName == null) {
            return null;
        }
        return mostRecentByPlayerAndIntent.get(playerId.toString() + ":" + intentName.toLowerCase());
    }

    public void clearPlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }
        String prefix = playerId.toString() + ":";
        for (String key : new ArrayList<String>(mostRecentByPlayerAndIntent.keySet())) {
            if (key.startsWith(prefix)) {
                mostRecentByPlayerAndIntent.remove(key);
            }
        }
        selector.clearPrefix(prefix);
    }
}
