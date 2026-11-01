package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class SetSpawnIntent extends AbstractIntent {

    public SetSpawnIntent() {
        super("set-spawn", Arrays.asList("set spawn", "mark spawn"), "compudah.intent.spawn.set");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Location location = player.getLocation();
        player.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        IntentSupport.complete(player, plugin, getName(), player.getWorld().getName());
    }
}
