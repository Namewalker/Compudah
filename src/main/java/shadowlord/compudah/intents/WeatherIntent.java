package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class WeatherIntent extends AbstractIntent {

    public WeatherIntent() {
        super("weather", Arrays.asList("weather", "forecast"), "compudah.intent.weather");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        boolean storm = player.getWorld().hasStorm();
        plugin.getResponseManager().send(player, storm ? "The weather is stormy and dramatic." : "The sky is clear. Very calm. Slightly suspicious.");
        IntentSupport.complete(player, plugin, getName(), "world-weather");
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        org.bukkit.World world = IntentSupport.getRemoteWorld(plugin);
        if (world == null) {
            plugin.getResponseManager().send(null, "There is no loaded world whose weather I can inspect.");
            return;
        }
        plugin.getResponseManager().send(null, world.hasStorm() ? "The weather in " + world.getName() + " is stormy and dramatic."
                : "The sky in " + world.getName() + " is clear. Very calm. Slightly suspicious.");
        IntentSupport.complete(context, plugin, getName(), world.getName() + "-weather");
    }
}
