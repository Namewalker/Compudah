package shadowlord.compudah.intents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class TeleportIntent extends AbstractIntent {

    public TeleportIntent() {
        super("teleport", Arrays.asList("teleport", "tp", "move", "portal", "send me", "take me", "transport me"), "compudah.intent.teleport");
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
        if (arguments.length >= 3 && arguments[1].equalsIgnoreCase("to")
                && (arguments[2].equalsIgnoreCase("me") || arguments[2].equalsIgnoreCase("myself"))) {
            Player traveler = IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
            if (traveler == null) {
                plugin.getResponseManager().send(player, "I cannot find the passenger. The pickup team is circling the airport.");
                return;
            }
            traveler.teleport(player.getLocation());
            IntentSupport.complete(player, plugin, getName(), traveler.getName());
            return;
        }
        String[] destination = java.util.Arrays.copyOfRange(arguments, targetStart, arguments.length);
        String destinationName = IntentSupport.join(destination);
        if (destinationName.equalsIgnoreCase("spawn")) {
            Location spawn = player.getWorld().getSpawnLocation();
            player.teleport(spawn);
            IntentSupport.complete(player, plugin, getName(), "spawn");
            return;
        }

        Player target = IntentSupport.resolveTarget(player, destination, plugin);
        if (target == null) {
            plugin.getResponseManager().send(player, "I have absolutely no idea where you're trying to go.");
            return;
        }

        player.teleport(target.getLocation());
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length < 3) {
            plugin.getResponseManager().send(null, "Use: teleport <online player> to <online player or spawn>.");
            return;
        }
        Player traveler = IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        int destinationIndex = arguments[1].equalsIgnoreCase("to") ? 2 : 1;
        if (traveler == null || destinationIndex >= arguments.length) {
            plugin.getResponseManager().send(null, "I could not identify the online traveler or destination.");
            return;
        }
        String destinationName = arguments[destinationIndex];
        if (destinationName.equalsIgnoreCase("spawn")) {
            traveler.teleport(traveler.getWorld().getSpawnLocation());
        } else {
            Player destination = IntentSupport.resolveTarget(context, java.util.Arrays.copyOfRange(arguments, destinationIndex, arguments.length), plugin);
            if (destination == null) {
                plugin.getResponseManager().send(null, "I could not locate the destination player.");
                return;
            }
            traveler.teleport(destination.getLocation());
        }
        IntentSupport.complete(context, plugin, getName(), traveler.getName() + " to " + destinationName);
    }
}
