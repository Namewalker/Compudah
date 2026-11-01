package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

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
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length < 3 || !arguments[1].equalsIgnoreCase("to")) {
            plugin.getResponseManager().send(null, "Use: bring <online player> to <another online player>.");
            return;
        }
        Player traveler = IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        Player destination = IntentSupport.resolveTarget(context, new String[] {arguments[2]}, plugin);
        if (traveler == null || destination == null) {
            plugin.getResponseManager().send(null, "I could not locate both online players.");
            return;
        }
        traveler.teleport(destination.getLocation());
        IntentSupport.complete(context, plugin, getName(), traveler.getName() + " to " + destination.getName());
    }
}
