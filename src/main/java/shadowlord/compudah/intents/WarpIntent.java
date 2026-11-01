package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

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
        IntentSupport.complete(player, plugin, getName(), arguments[0]);
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length < 2) {
            plugin.getResponseManager().send(null, "Use: warp <online player> <warp name>.");
            return;
        }
        Player target = IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        String path = "warps." + arguments[1].toLowerCase(Locale.ENGLISH);
        World world = Bukkit.getWorld(plugin.getConfig().getString(path + ".world", ""));
        if (target == null || world == null || !plugin.getConfig().contains(path + ".world")) {
            plugin.getResponseManager().send(null, "I could not find that online player or saved warp.");
            return;
        }
        Location location = new Location(world, plugin.getConfig().getDouble(path + ".x"), plugin.getConfig().getDouble(path + ".y"),
                plugin.getConfig().getDouble(path + ".z"), (float) plugin.getConfig().getDouble(path + ".yaw"),
                (float) plugin.getConfig().getDouble(path + ".pitch"));
        target.teleport(location);
        IntentSupport.complete(context, plugin, getName(), target.getName() + " to " + arguments[1]);
    }
}
