package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class ServerInfoIntent extends AbstractIntent {

    public ServerInfoIntent() {
        super("server", Arrays.asList("server", "server info", "server version", "version", "how many players"), "compudah.intent.server");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        plugin.getResponseManager().send(player, "Server: " + Bukkit.getName() + " | Minecraft: " + Bukkit.getBukkitVersion()
                + " | Online: " + Bukkit.getOnlinePlayers().size() + ". The machinery is functioning, more or less.");
        IntentSupport.complete(player, plugin, getName(), "server-info");
    }
}
