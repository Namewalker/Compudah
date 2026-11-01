package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class StatusIntent extends AbstractIntent {

    public StatusIntent() {
        super("status", Arrays.asList("status", "stats", "info", "summary"), "compudah.use");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        String mode = plugin.getPersonalityManager() == null ? "cheerful" : plugin.getPersonalityManager().getMode();
        String line = "Compudah is online in " + mode + " mode. I currently have " + plugin.getIntentRegistry().getIntents().size() + " active intents and I remain highly opinionated.";
        plugin.getResponseManager().send(player, line);
        IntentSupport.complete(player, plugin, getName(), "system");
    }
}
