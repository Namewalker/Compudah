package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.IntentContext;

public final class IntentSupport {

    private IntentSupport() {
    }

    public static Player resolveTarget(Player requester, String[] arguments, CompudahPlugin plugin) {
        return resolveTarget(requester == null ? null : IntentContext.forInGame(requester), arguments, plugin);
    }

    public static Player resolveTarget(IntentContext requester, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            return requester == null ? null : requester.getPlayer();
        }
        String query = join(arguments).toLowerCase(Locale.ENGLISH).trim();
        int destinationDelimiter = query.indexOf(" to ");
        if (destinationDelimiter > 0) {
            query = query.substring(0, destinationDelimiter).trim();
        }
        if (query.equals("me") || query.equals("myself") || query.equals("self")) {
            return requester == null ? null : requester.getPlayer();
        }
        if (query.equals("him") || query.equals("her") || query.equals("them") || query.equals("he")
                || query.equals("she") || query.equals("they") || query.equals("it")) {
            if (requester == null) {
                return null;
            }
            query = plugin.getPlayerContextMemory().resolveReference(requester.getUniqueId(), query);
            if (query == null) {
                return null;
            }
        }

        List<String> names = new ArrayList<String>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            names.add(online.getName());
        }
        FuzzyPlayerMatcher.MatchResult match = plugin.getFuzzyPlayerMatcher().match(query, names);
        return match == null || match.getMatchedName() == null ? null : Bukkit.getPlayerExact(match.getMatchedName());
    }

    public static String join(String[] values) {
        StringBuilder joined = new StringBuilder();
        for (String value : values) {
            if (joined.length() > 0) {
                joined.append(' ');
            }
            joined.append(value);
        }
        return joined.toString();
    }

    public static World getRemoteWorld(CompudahPlugin plugin) {
        String configuredName = plugin.getConfig().getString("discordsrv.default-world", "");
        World configured = configuredName.isEmpty() ? null : Bukkit.getWorld(configuredName);
        if (configured != null) {
            return configured;
        }
        return Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
    }

    public static void complete(Player player, CompudahPlugin plugin, String intent, String target) {
        if (player == null) {
            return;
        }
        complete(IntentContext.forInGame(player), plugin, intent, target);
    }

    public static void complete(IntentContext actor, CompudahPlugin plugin, String intent, String target) {
        if (actor == null) {
            return;
        }
        boolean immediate = plugin.getVariantManager().isImmediateExecution();
        if (!immediate && actor.getPlayer() != null) {
            plugin.getDramaticEventManager().celebrate(actor.getPlayer(), intent, target);
        }
        plugin.getLoggingService().logAction(actor.getName(), intent, target, true);
        if (plugin.getResponseGenerator() != null) {
            plugin.getResponseManager().send(actor.getPlayer(), plugin.getResponseGenerator().generate(actor, intent, target,
                    immediate || actor.getPlayer() == null ? null : plugin.getVariantManager().getMostRecent(actor, intent)));
        }
    }
}
