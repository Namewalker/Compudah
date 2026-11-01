package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class SetTimeIntent extends AbstractIntent {

    public SetTimeIntent() {
        super("set-time", Arrays.asList("set time", "time set", "make it"), "compudah.intent.time.set");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(player, "Set it to day, night, noon, midnight, or give me a tick count.");
            return;
        }
        String requested = IntentSupport.join(arguments).toLowerCase(Locale.ENGLISH);
        long time;
        if (requested.equals("day") || requested.equals("morning")) {
            time = 1000L;
        } else if (requested.equals("noon")) {
            time = 6000L;
        } else if (requested.equals("night") || requested.equals("evening")) {
            time = 13000L;
        } else if (requested.equals("midnight")) {
            time = 18000L;
        } else {
            try {
                time = Long.parseLong(arguments[0]);
                if (time < 0L || time > 23999L) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                plugin.getResponseManager().send(player, "That is not a valid Minecraft time. The sun refuses to negotiate.");
                return;
            }
        }
        player.getWorld().setTime(time);
        plugin.getResponseManager().send(player, "Time adjusted. The celestial schedule has been strongly encouraged.");
        IntentSupport.complete(player, plugin, getName(), requested);
    }
}
