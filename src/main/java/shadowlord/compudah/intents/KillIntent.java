package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class KillIntent extends AbstractIntent {

    public KillIntent() {
        super("kill", Arrays.asList("kill", "eliminate", "destroy", "delete", "remove", "smite", "obliterate",
            "erase", "get rid of", "make disappear", "send to jesus"), "compudah.intent.kill");
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

        target.damage(9999.0D, player);
        plugin.getPlayerContextMemory().remember(player.getUniqueId(), target.getName());
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = IntentSupport.resolveTarget(context, arguments, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "Name an online player. I cannot apply a tactical removal to an offline account.");
            return;
        }
        target.damage(9999.0D);
        plugin.getPlayerContextMemory().remember(context.getUniqueId(), target.getName());
        IntentSupport.complete(context, plugin, getName(), target.getName());
    }
}
