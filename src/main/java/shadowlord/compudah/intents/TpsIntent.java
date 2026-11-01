package shadowlord.compudah.intents;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class TpsIntent extends AbstractIntent {

    public TpsIntent() {
        super("tps", Arrays.asList("tps", "server tps", "ticks per second", "lag"), "compudah.intent.server");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        try {
            Method getTps = Bukkit.getServer().getClass().getMethod("getTPS");
            Object result = getTps.invoke(Bukkit.getServer());
            if (result instanceof double[] && ((double[]) result).length >= 3) {
                double[] tps = (double[]) result;
                plugin.getResponseManager().send(player, String.format("Server TPS: %.2f (1m), %.2f (5m), %.2f (15m). The server is holding it together.",
                        tps[0], tps[1], tps[2]));
            } else {
                plugin.getResponseManager().send(player, "This server build does not expose TPS readings. Its secrets are well protected.");
            }
        } catch (ReflectiveOperationException exception) {
            plugin.getResponseManager().send(player, "This server build does not expose TPS readings. Its secrets are well protected.");
        }
        IntentSupport.complete(player, plugin, getName(), "server-tps");
    }
}
