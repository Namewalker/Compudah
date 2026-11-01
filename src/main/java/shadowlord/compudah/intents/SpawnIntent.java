package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class SpawnIntent extends AbstractIntent {

    public SpawnIntent() {
        super("spawn", Arrays.asList("spawn", "respawn"), "compudah.intent.spawn");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        player.teleport(player.getWorld().getSpawnLocation());
        IntentSupport.complete(player, plugin, getName(), "world-spawn");
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
                : IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "Name an online player to send to spawn; your offline character has no current location.");
            return;
        }
        target.teleport(target.getWorld().getSpawnLocation());
        IntentSupport.complete(context, plugin, getName(), target.getName());
    }
}
