package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

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
}
