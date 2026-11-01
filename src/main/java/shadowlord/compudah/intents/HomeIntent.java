package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class HomeIntent extends AbstractIntent {

    public HomeIntent() {
        super("home", Arrays.asList("home", "my home", "go home"), "compudah.intent.home");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        String path = "homes." + player.getUniqueId();
        if (!plugin.getConfig().contains(path + ".world")) {
            plugin.getResponseManager().send(player, "You have no saved home yet. Use 'set home' and I will pin reality down for you.");
            return;
        }
        World world = Bukkit.getWorld(plugin.getConfig().getString(path + ".world"));
        if (world == null) {
            plugin.getResponseManager().send(player, "Your home world is unavailable. I refuse to teleport you into the void.");
            return;
        }
        Location location = new Location(world, plugin.getConfig().getDouble(path + ".x"), plugin.getConfig().getDouble(path + ".y"),
                plugin.getConfig().getDouble(path + ".z"), (float) plugin.getConfig().getDouble(path + ".yaw"),
                (float) plugin.getConfig().getDouble(path + ".pitch"));
        player.teleport(location);
        plugin.getResponseManager().send(player, "Home, sweet home. I have reduced the dramatic music by 12 percent.");
        IntentSupport.complete(player, plugin, getName(), "home");
    }
}
