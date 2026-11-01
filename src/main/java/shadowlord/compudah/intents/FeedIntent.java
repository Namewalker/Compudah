package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class FeedIntent extends AbstractIntent {

    public FeedIntent() {
        super("feed", Arrays.asList("feed", "eat", "snack", "nourish"), "compudah.intent.feed");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = IntentSupport.resolveTarget(player, arguments, plugin);

        if (target == null) {
            plugin.getResponseManager().send(player, "I have absolutely no idea who you're talking about.");
            return;
        }

        target.setFoodLevel(20);
        target.setSaturation(20.0F);
        plugin.getPlayerContextMemory().remember(player.getUniqueId(), target.getName());
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = IntentSupport.resolveTarget(context, arguments, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "Name an online player to feed. Offline characters have very small mouths.");
            return;
        }
        target.setFoodLevel(20);
        target.setSaturation(20.0F);
        plugin.getPlayerContextMemory().remember(context.getUniqueId(), target.getName());
        IntentSupport.complete(context, plugin, getName(), target.getName());
    }
}
