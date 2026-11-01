package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

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
}
