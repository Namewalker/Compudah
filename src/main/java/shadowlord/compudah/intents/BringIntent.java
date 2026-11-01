package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class BringIntent extends AbstractIntent {

    public BringIntent() {
        super("bring", Arrays.asList("bring", "summon", "come here", "pull"), "compudah.intent.teleport");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
            : IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
        if (target == null) {
            plugin.getResponseManager().send(player, "I could not identify who to summon. My teleporter is disappointed.");
            return;
        }
        target.teleport(player.getLocation());
        plugin.getResponseManager().send(player, "Summoning complete. Please welcome " + target.getName() + " to your immediate vicinity.");
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }
}
