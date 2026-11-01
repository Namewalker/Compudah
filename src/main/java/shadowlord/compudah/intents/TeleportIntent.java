package shadowlord.compudah.intents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class TeleportIntent extends AbstractIntent {

    public TeleportIntent() {
        super("teleport", Arrays.asList("teleport", "tp", "move", "portal"), "compudah.intent.teleport");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(player, "Slight issue. You forgot the destination. Try 'spawn', 'home', or a player's name.");
            return;
        }

        int targetStart = 0;
        if (arguments.length > 1 && (arguments[0].equalsIgnoreCase("me") || arguments[0].equalsIgnoreCase("myself"))
                && arguments[1].equalsIgnoreCase("to")) {
            targetStart = 2;
        } else if (arguments[0].equalsIgnoreCase("to") || arguments[0].equalsIgnoreCase("player")) {
            targetStart = 1;
        }
        if (targetStart >= arguments.length) {
            plugin.getResponseManager().send(player, "Destination missing. I have prepared the portal and absolutely nowhere to send it.");
            return;
        }
        String[] destination = java.util.Arrays.copyOfRange(arguments, targetStart, arguments.length);
        String destinationName = IntentSupport.join(destination);
        if (destinationName.equalsIgnoreCase("spawn")) {
            Location spawn = player.getWorld().getSpawnLocation();
            player.teleport(spawn);
            plugin.getResponseManager().send(player, "You have been relocated to spawn. Try not to magically vanish again.");
            IntentSupport.complete(player, plugin, getName(), "spawn");
            return;
        }

        Player target = IntentSupport.resolveTarget(player, destination, plugin);
        if (target == null) {
            plugin.getResponseManager().send(player, "I have absolutely no idea where you're trying to go.");
            return;
        }

        player.teleport(target.getLocation());
        plugin.getResponseManager().send(player, "Hyper-speed transport complete. Please keep all limbs inside the vehicle.");
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }
}
