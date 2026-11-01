package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class SpawnIntent extends AbstractIntent {

    public SpawnIntent() {
        super("spawn", Arrays.asList("spawn", "respawn"), "compudah.intent.spawn");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        player.teleport(player.getWorld().getSpawnLocation());
        plugin.getResponseManager().send(player, "Spawn delivery complete. Please remain inside the designated reality.");
        IntentSupport.complete(player, plugin, getName(), "world-spawn");
    }
}
