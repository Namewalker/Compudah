package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class UnmuteIntent extends AbstractIntent {

    public UnmuteIntent() {
        super("unmute", Arrays.asList("unmute", "unsilence"), "compudah.intent.mute");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
            : IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
        if (target == null) {
            plugin.getResponseManager().send(player, "Name an online player to unmute.");
            return;
        }
        if (plugin.getMuteManager().unmute(target.getUniqueId())) {
            IntentSupport.complete(player, plugin, getName(), target.getName());
        } else {
            plugin.getResponseManager().send(player, "That player was not muted by Compudah.");
        }
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
                : IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "Name an online player to unmute.");
            return;
        }
        if (plugin.getMuteManager().unmute(target.getUniqueId())) {
            IntentSupport.complete(context, plugin, getName(), target.getName());
        } else {
            plugin.getResponseManager().send(null, target.getName() + " was not muted by Compudah.");
        }
    }
}
