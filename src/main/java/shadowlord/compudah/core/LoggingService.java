package shadowlord.compudah.core;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;

public class LoggingService {

    private final CompudahPlugin plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LoggingService(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    public void logAction(Player player, String intentName, String target, boolean success) {
        if (player == null) {
            return;
        }
        logAction(player.getName(), intentName, target, success);
    }

    public void logAction(String actorName, String intentName, String target, boolean success) {
        String message = String.format("Player: %s | Intent: %s | Target: %s | Success: %s | Time: %s",
                actorName == null ? "unknown" : actorName, intentName, target == null ? "N/A" : target, success, dateFormat.format(new Date()));
        plugin.getLogger().info(message);

        if (plugin.getConfig().getBoolean("logging.file", false)) {
            File logFile = new File(plugin.getDataFolder(), "compudah.log");
            try {
                plugin.getDataFolder().mkdirs();
                java.nio.file.Files.write(logFile.toPath(), (message + System.lineSeparator()).getBytes("UTF-8"),
                        java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
            } catch (IOException ex) {
                plugin.getLogger().warning("Failed to write Compudah log file: " + ex.getMessage());
            }
        }
    }
}
