package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class GameModeIntent extends AbstractIntent {

    public GameModeIntent() {
        super("gamemode", Arrays.asList("gamemode", "game mode"), "compudah.intent.gamemode");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        String value = arguments == null || arguments.length == 0 ? "" : arguments[0].toLowerCase(Locale.ENGLISH);
        GameMode mode;
        if (value.isEmpty()) {
            plugin.getResponseManager().send(player, "Please specify creative, survival, adventure, or spectator. I am not psychic; I am merely impressive.");
            return;
        }
        try {
            mode = GameMode.valueOf(value.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exception) {
            plugin.getResponseManager().send(player, "Choose creative, survival, adventure, or spectator.");
            return;
        }
        player.setGameMode(mode);
        IntentSupport.complete(player, plugin, getName(), mode.name());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length < 2) {
            plugin.getResponseManager().send(null, "Use: gamemode <online player> <creative|survival|adventure|spectator>.");
            return;
        }
        Player target = IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        GameMode mode;
        try {
            mode = GameMode.valueOf(arguments[1].toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exception) {
            plugin.getResponseManager().send(null, "Choose creative, survival, adventure, or spectator.");
            return;
        }
        if (target == null) {
            plugin.getResponseManager().send(null, "I could not find that online player.");
            return;
        }
        target.setGameMode(mode);
        IntentSupport.complete(context, plugin, getName(), target.getName() + " / " + mode.name());
    }
}
