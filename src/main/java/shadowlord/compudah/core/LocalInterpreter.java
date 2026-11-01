package shadowlord.compudah.core;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;
import shadowlord.compudah.api.IntentInterpreter;

public class LocalInterpreter implements IntentInterpreter {

    private final CompudahPlugin plugin;
    private final IntentRegistry intentRegistry;
    private final WakeWordDetector wakeWordDetector;

    public LocalInterpreter(CompudahPlugin plugin, IntentRegistry intentRegistry, WakeWordDetector wakeWordDetector) {
        this.plugin = plugin;
        this.intentRegistry = intentRegistry;
        this.wakeWordDetector = wakeWordDetector;
    }

    public void interpret(Player player, String rawMessage) {
        if (player == null || rawMessage == null || rawMessage.trim().isEmpty()) {
            return;
        }

        String command = stripPolitePrefix(wakeWordDetector.extractCommand(rawMessage));
        if (command == null || command.trim().isEmpty()) {
            plugin.getResponseManager().send(player, "I am listening. Tell me what you want, and do try to be less vague.");
            return;
        }

        IntentRegistry.Resolution resolution = intentRegistry.resolveCommand(command);
        Intent intent = resolution.getIntent();
        if (intent == null) {
            plugin.getResponseManager().send(player, "I do not understand that request. Try 'heal', 'feed', 'set time day', 'spawn', or 'online'.");
            return;
        }

        if (!player.hasPermission(intent.getPermission())) {
            plugin.getResponseManager().send(player, "You are not authorized for that particular bit of chaos.");
            return;
        }

        if (!intent.isEnabled()) {
            plugin.getResponseManager().send(player, "That action is currently unavailable. The bureaucracy has spoken.");
            return;
        }

        if (!player.isOp() && !player.hasPermission("compudah.admin")
                && !plugin.getConfig().getStringList("allowed-player-intents").contains(intent.getName())) {
            plugin.getResponseManager().send(player, "That request is outside your current Compudah privileges.");
            return;
        }
        if (!plugin.getConfig().getBoolean("intents." + intent.getName() + ".enabled", true)) {
            plugin.getResponseManager().send(player, "That action is currently unavailable. The bureaucracy has spoken.");
            return;
        }

        if (!plugin.getCooldownManager().canUse(player.getUniqueId(), player.isOp() || player.hasPermission("compudah.admin"))) {
            plugin.getResponseManager().send(player, "Compudah is still recharging. Try again in a moment.");
            return;
        }

        intent.execute(player, resolution.getArguments(), plugin);
    }

    private String stripPolitePrefix(String command) {
        String normalized = command == null ? "" : command.trim();
        String[] prefixes = {"please ", "can you ", "could you ", "would you ", "will you ",
                "could you please ", "would you please ", "do me a favor and "};
        boolean removed;
        do {
            removed = false;
            for (String prefix : prefixes) {
                if (normalized.toLowerCase().startsWith(prefix)) {
                    normalized = normalized.substring(prefix.length()).trim();
                    removed = true;
                    break;
                }
            }
        } while (removed);
        return normalized;
    }
}
