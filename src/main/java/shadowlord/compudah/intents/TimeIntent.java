package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class TimeIntent extends AbstractIntent {

    public TimeIntent() {
        super("time", Arrays.asList("time", "clock", "date"), "compudah.intent.time");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        long time = player.getWorld().getTime();
        plugin.getResponseManager().send(player, "The current in-game time is " + time + ".");
        IntentSupport.complete(player, plugin, getName(), "world-time");
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        org.bukkit.World world = IntentSupport.getRemoteWorld(plugin);
        if (world == null) {
            plugin.getResponseManager().send(null, "There is no loaded world whose time I can check.");
            return;
        }
        plugin.getResponseManager().send(null, "The in-game time in " + world.getName() + " is " + world.getTime() + ".");
        IntentSupport.complete(context, plugin, getName(), world.getName() + "-time");
    }
}
