package shadowlord.compudah.api;

import org.bukkit.entity.Player;

public interface IntentInterpreter {

    void interpret(Player player, String rawMessage);
}
