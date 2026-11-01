package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class SetWeatherIntent extends AbstractIntent {

    public SetWeatherIntent() {
        super("set-weather", Arrays.asList("set weather", "weather set"), "compudah.intent.weather.set");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        String weather = arguments == null || arguments.length == 0 ? "" : arguments[0].toLowerCase(Locale.ENGLISH);
        if (weather.equals("clear") || weather.equals("sun") || weather.equals("sunny")) {
            player.getWorld().setStorm(false);
            player.getWorld().setThundering(false);
        } else if (weather.equals("rain") || weather.equals("storm")) {
            player.getWorld().setStorm(true);
            player.getWorld().setThundering(false);
        } else if (weather.equals("thunder") || weather.equals("thunderstorm")) {
            player.getWorld().setStorm(true);
            player.getWorld().setThundering(true);
        } else {
            plugin.getResponseManager().send(player, "Choose clear, rain, or thunder. The cloud department needs specifics.");
            return;
        }
        player.getWorld().setWeatherDuration(12000);
        plugin.getResponseManager().send(player, "Weather set to " + weather + ". The atmosphere has been informed.");
        IntentSupport.complete(player, plugin, getName(), weather);
    }
}
