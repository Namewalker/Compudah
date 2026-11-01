package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

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
        plugin.getResponseManager().send(player, "Game mode changed to " + mode.name().toLowerCase(Locale.ENGLISH) + ". Reality has been reclassified.");
        IntentSupport.complete(player, plugin, getName(), mode.name());
    }
}
