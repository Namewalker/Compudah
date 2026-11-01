package shadowlord.compudah.core;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.World;

import shadowlord.compudah.CompudahPlugin;

public class ResponseManager {

    private final CompudahPlugin plugin;

    public ResponseManager(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    public void send(Player player, String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        if (plugin.getResponseGenerator() != null) {
            String lower = message.toLowerCase();
            if (lower.startsWith("i do not understand that request")) {
                message = plugin.getResponseGenerator().generateError(player, "unknown");
            } else if (lower.contains("not authorized") || lower.contains("outside your current compudah privileges")) {
                message = plugin.getResponseGenerator().generateError(player, "permission");
            } else if (lower.contains("no idea who you're talking about") || lower.contains("could not identify who")
                    || lower.contains("cannot find the passenger")) {
                message = plugin.getResponseGenerator().generateError(player, "target");
            } else if (lower.contains("still recharging")) {
                message = plugin.getResponseGenerator().generateError(player, "cooldown");
            }
        }
        String formatted = ChatColor.translateAlternateColorCodes('&',
                message.startsWith("[Compudah]") ? message : "[Compudah] " + message);
        String audience = plugin.getConfig().getString("message-audience", "global").toLowerCase();
        if ("local".equals(audience) && player != null) {
            double radius = Math.max(1.0D, plugin.getConfig().getDouble("message-radius", 64.0D));
            double radiusSquared = radius * radius;
            World world = player.getWorld();
            for (Player recipient : world.getPlayers()) {
                if (recipient.getLocation().distanceSquared(player.getLocation()) <= radiusSquared) {
                    recipient.sendMessage(formatted);
                }
            }
        } else if ("world".equals(audience) && player != null) {
            for (Player recipient : player.getWorld().getPlayers()) {
                recipient.sendMessage(formatted);
            }
        } else {
            Bukkit.broadcastMessage(formatted);
        }
    }

    public void broadcast(String message) {
        send(null, message);
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
