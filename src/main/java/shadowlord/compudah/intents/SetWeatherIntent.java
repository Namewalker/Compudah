package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

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
        IntentSupport.complete(player, plugin, getName(), weather);
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        org.bukkit.World world = IntentSupport.getRemoteWorld(plugin);
        String weather = arguments == null || arguments.length == 0 ? "" : arguments[0].toLowerCase(Locale.ENGLISH);
        if (world == null) {
            plugin.getResponseManager().send(null, "There is no configured default world for remote weather control.");
            return;
        }
        if (weather.equals("clear") || weather.equals("sun") || weather.equals("sunny")) {
            world.setStorm(false);
            world.setThundering(false);
        } else if (weather.equals("rain") || weather.equals("storm")) {
            world.setStorm(true);
            world.setThundering(false);
        } else if (weather.equals("thunder") || weather.equals("thunderstorm")) {
            world.setStorm(true);
            world.setThundering(true);
        } else {
            plugin.getResponseManager().send(null, "Choose clear, rain, or thunder.");
            return;
        }
        world.setWeatherDuration(12000);
        IntentSupport.complete(context, plugin, getName(), world.getName() + ":" + weather);
    }
}
