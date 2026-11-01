package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class KillIntent extends AbstractIntent {

    public KillIntent() {
        super("kill", Arrays.asList("kill", "eliminate", "destroy", "delete", "remove", "smite", "obliterate"), "compudah.intent.kill");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(player, "Wonderful plan. Who exactly should die?");
            return;
        }

        Player target = IntentSupport.resolveTarget(player, arguments, plugin);
        if (target == null) {
            plugin.getResponseManager().send(player, "I have absolutely no idea who you're talking about.");
            return;
        }

        final Player selectedTarget = target;
        Runnable finishKill = new Runnable() {
            @Override
            public void run() {
                if (!selectedTarget.isOnline() || !selectedTarget.isValid()) {
                    player.sendMessage("[Compudah] Target escaped the blast radius. I shall pretend that was intentional.");
                    return;
                }
                selectedTarget.damage(9999.0D, player);
                plugin.getPlayerContextMemory().remember(player.getUniqueId(), selectedTarget.getName());
                plugin.getResponseManager().send(player, "Violence selected. " + selectedTarget.getName() + " has entered a rather unfortunate phase of existence.");
                IntentSupport.complete(player, plugin, getName(), selectedTarget.getName());
            }
        };

        if (!plugin.getDramaticEventManager().runOrbitalSequence(player, selectedTarget, finishKill)) {
            finishKill.run();
        }
    }
}
