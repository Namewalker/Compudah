package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class MuteIntent extends AbstractIntent {

    public MuteIntent() {
        super("mute", Arrays.asList("mute", "silence"), "compudah.intent.mute");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
            : IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
        if (target == null || target == player) {
            plugin.getResponseManager().send(player, "Name another online player. I refuse to mute my favorite operator.");
            return;
        }
        if (plugin.getMuteManager().mute(target.getUniqueId())) {
            IntentSupport.complete(player, plugin, getName(), target.getName());
        } else {
            plugin.getResponseManager().send(player, target.getName() + " is already muted. Efficiency!");
        }
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
                : IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        if (target == null || target.getUniqueId().equals(context.getUniqueId())) {
            plugin.getResponseManager().send(null, "Name another online player to mute.");
            return;
        }
        if (plugin.getMuteManager().mute(target.getUniqueId())) {
            IntentSupport.complete(context, plugin, getName(), target.getName());
        } else {
            plugin.getResponseManager().send(null, target.getName() + " is already muted.");
        }
    }
}
