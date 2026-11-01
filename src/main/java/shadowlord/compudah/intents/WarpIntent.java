package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class WarpIntent extends AbstractIntent {

    public WarpIntent() {
        super("warp", Arrays.asList("warp", "go to warp"), "compudah.intent.warp");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(player, "Name a warp. I am a teleportation assistant, not a mind reader.");
            return;
        }
        String path = "warps." + arguments[0].toLowerCase(Locale.ENGLISH);
        World world = Bukkit.getWorld(plugin.getConfig().getString(path + ".world", ""));
        if (!plugin.getConfig().contains(path + ".world") || world == null) {
            plugin.getResponseManager().send(player, "That warp does not exist, or its world has gone missing.");
            return;
        }
        Location location = new Location(world, plugin.getConfig().getDouble(path + ".x"), plugin.getConfig().getDouble(path + ".y"),
                plugin.getConfig().getDouble(path + ".z"), (float) plugin.getConfig().getDouble(path + ".yaw"),
                (float) plugin.getConfig().getDouble(path + ".pitch"));
        player.teleport(location);
        plugin.getResponseManager().send(player, "Warp " + arguments[0] + " complete. Spatial integrity appears acceptable.");
        IntentSupport.complete(player, plugin, getName(), arguments[0]);
    }
}
