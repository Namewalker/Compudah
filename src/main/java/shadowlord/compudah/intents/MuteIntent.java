package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

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
            plugin.getResponseManager().send(player, target.getName() + " has been muted until this server restarts. The silence is almost musical.");
            target.sendMessage("[Compudah] You have been muted by server staff until the next restart.");
            IntentSupport.complete(player, plugin, getName(), target.getName());
        } else {
            plugin.getResponseManager().send(player, target.getName() + " is already muted. Efficiency!");
        }
    }
}
