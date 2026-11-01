package shadowlord.compudah.core;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ResponseManager {

    public void send(Player player, String message) {
        if (player == null || message == null || message.trim().isEmpty()) {
            return;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "[Compudah] " + message));
    }

    public String replacePlaceholders(String template, Map<String, String> values) {
        if (template == null) {
            return "";
        }
        String result = template;
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                result = result.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
            }
        }
        return result;
    }
}
