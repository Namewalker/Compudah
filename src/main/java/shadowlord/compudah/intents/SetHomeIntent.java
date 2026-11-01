package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class SetHomeIntent extends AbstractIntent {

    public SetHomeIntent() {
        super("set-home", Arrays.asList("set home", "sethome", "save home"), "compudah.intent.home");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        String path = "homes." + player.getUniqueId();
        Location location = player.getLocation();
        plugin.getConfig().set(path + ".world", player.getWorld().getName());
        plugin.getConfig().set(path + ".x", location.getX());
        plugin.getConfig().set(path + ".y", location.getY());
        plugin.getConfig().set(path + ".z", location.getZ());
        plugin.getConfig().set(path + ".yaw", location.getYaw());
        plugin.getConfig().set(path + ".pitch", location.getPitch());
        plugin.saveConfig();
        plugin.getResponseManager().send(player, "Home saved. I have memorized your coordinates and will pretend this is not sentimental.");
        IntentSupport.complete(player, plugin, getName(), "home");
    }
}
