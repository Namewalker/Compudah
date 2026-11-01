package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;

public final class IntentSupport {

    private IntentSupport() {
    }

    public static Player resolveTarget(Player requester, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            return requester;
        }
        String query = join(arguments).toLowerCase(Locale.ENGLISH);
        if (query.equals("me") || query.equals("myself") || query.equals("self")) {
            return requester;
        }
        if (query.equals("him") || query.equals("her") || query.equals("them") || query.equals("he")
                || query.equals("she") || query.equals("they") || query.equals("it")) {
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

    public static void complete(Player player, CompudahPlugin plugin, String intent, String target) {
        plugin.getDramaticEventManager().celebrate(player, intent, target);
        plugin.getLoggingService().logAction(player, intent, target, true);
    }
}
