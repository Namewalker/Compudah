package shadowlord.compudah.intents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class OnlinePlayersIntent extends AbstractIntent {

    public OnlinePlayersIntent() {
        super("online", Arrays.asList("online", "players", "who", "list"), "compudah.intent.online");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        List<String> names = new ArrayList<String>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            names.add(onlinePlayer.getName());
        }

        if (names.isEmpty()) {
            plugin.getResponseManager().send(player, "The server is empty. The silence is suspicious.");
            return;
        }

        plugin.getResponseManager().send(player, "Online players: " + String.join(", ", names));
        IntentSupport.complete(player, plugin, getName(), "server-list");
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        List<String> names = new ArrayList<String>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            names.add(onlinePlayer.getName());
        }
        plugin.getResponseManager().send(null, names.isEmpty() ? "The server is empty. The silence is suspicious."
                : "Online players: " + String.join(", ", names));
        IntentSupport.complete(context, plugin, getName(), "server-list");
    }
}
