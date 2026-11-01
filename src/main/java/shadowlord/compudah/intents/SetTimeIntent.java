package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

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
        IntentSupport.complete(player, plugin, getName(), requested);
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        org.bukkit.World world = IntentSupport.getRemoteWorld(plugin);
        if (world == null || arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(null, "Name a time and ensure a default world is loaded.");
            return;
        }
        String requested = IntentSupport.join(arguments).toLowerCase(Locale.ENGLISH);
        long time;
        if (requested.equals("day") || requested.equals("morning")) time = 1000L;
        else if (requested.equals("noon")) time = 6000L;
        else if (requested.equals("night") || requested.equals("evening")) time = 13000L;
        else if (requested.equals("midnight")) time = 18000L;
        else {
            try {
                time = Long.parseLong(arguments[0]);
                if (time < 0L || time > 23999L) throw new NumberFormatException();
            } catch (NumberFormatException exception) {
                plugin.getResponseManager().send(null, "That is not a valid Minecraft time.");
                return;
            }
        }
        world.setTime(time);
        IntentSupport.complete(context, plugin, getName(), world.getName());
    }
}
