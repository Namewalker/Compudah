package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class SetWarpIntent extends AbstractIntent {

    public SetWarpIntent() {
        super("set-warp", Arrays.asList("set warp", "create warp", "save warp"), "compudah.intent.warp.set");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0 || !arguments[0].matches("[A-Za-z0-9_-]{1,32}")) {
            plugin.getResponseManager().send(player, "Give the warp a simple name (letters, numbers, underscores, or hyphens; 32 characters max).");
            return;
        }
        String name = arguments[0].toLowerCase(Locale.ENGLISH);
        String path = "warps." + name;
        Location location = player.getLocation();
        plugin.getConfig().set(path + ".world", player.getWorld().getName());
        plugin.getConfig().set(path + ".x", location.getX());
        plugin.getConfig().set(path + ".y", location.getY());
        plugin.getConfig().set(path + ".z", location.getZ());
        plugin.getConfig().set(path + ".yaw", location.getYaw());
        plugin.getConfig().set(path + ".pitch", location.getPitch());
        plugin.saveConfig();
        plugin.getResponseManager().send(player, "Warp " + name + " saved. Spatial bureaucracy has been updated.");
        IntentSupport.complete(player, plugin, getName(), name);
    }
}
