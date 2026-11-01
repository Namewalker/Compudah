package shadowlord.compudah.core;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;
import shadowlord.compudah.api.IntentContext;
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
        interpret(player, rawMessage, ChatOrigin.IN_GAME);
    }

    public void interpret(Player player, String rawMessage, ChatOrigin origin) {
        if (player == null) {
            return;
        }
        IntentContext context = origin == ChatOrigin.DISCORD
                ? IntentContext.forDiscord(player) : IntentContext.forInGame(player);
        interpret(context, rawMessage, origin);
    }

    public void interpret(IntentContext context, String rawMessage, ChatOrigin origin) {
        if (context == null || rawMessage == null || rawMessage.trim().isEmpty()) {
            return;
        }
        Player player = context.getPlayer();
        boolean inGameOperator = ChatAuthority.isInGameOperator(origin, context.isInGameOperator());

        String command = stripPolitePrefix(wakeWordDetector.extractCommand(rawMessage));
        ImmediateRequest immediateRequest = removeImmediatePhrases(command);
        command = immediateRequest.getCommand();
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

        if (!intent.isEnabled()) {
            plugin.getResponseManager().send(player, "That action is currently unavailable. The bureaucracy has spoken.");
            return;
        }

        if (!ChatAuthority.canUseIntent(origin, context.isInGameOperator(), intent.getName(),
            plugin.getConfig().getStringList("allowed-player-intents"))) {
            plugin.getResponseManager().send(player, "That request is outside your current Compudah privileges.");
            return;
        }
        if (!plugin.getConfig().getBoolean("intents." + intent.getName() + ".enabled", true)) {
            plugin.getResponseManager().send(player, "That action is currently unavailable. The bureaucracy has spoken.");
            return;
        }

        if (!inGameOperator) {
            int cooldownSeconds = plugin.getConfig().getInt("cooldowns." + intent.getName(),
                    plugin.getConfig().getInt("cooldowns.default", 3));
            if (!plugin.getCooldownManager().canUse(context.getUniqueId(), intent.getName(), cooldownSeconds)) {
                plugin.getResponseManager().send(player, "Compudah is still recharging. Try again in a moment.");
                return;
            }
        }

        if (immediateRequest.isImmediate()) {
            plugin.getResponseManager().send(player, "Aww. I was going to use the " + intent.getName() + " theatrics. Speed requested. Fun denied.");
        }

        if (plugin.getVariantManager() == null) {
            intent.execute(context, resolution.getArguments(), plugin);
            return;
        }
        try {
            plugin.getVariantManager().execute(context, resolution.getArguments(), plugin, intent, immediateRequest.isImmediate());
        } catch (UnsupportedOperationException exception) {
            plugin.getResponseManager().send(player, "That custom intent requires you to be online in-game.");
        }
    }

    private ImmediateRequest removeImmediatePhrases(String command) {
        if (command == null) {
            return new ImmediateRequest("", false);
        }
        String cleaned = command.replaceAll("(?i)\\b(without flair|skip theatrics|skip the show|immediately|instantly|quickly|now)\\b", " ");
        boolean immediate = !cleaned.equals(command);
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return new ImmediateRequest(cleaned, immediate);
    }

    private static final class ImmediateRequest {
        private final String command;
        private final boolean immediate;

        private ImmediateRequest(String command, boolean immediate) {
            this.command = command;
            this.immediate = immediate;
        }

        private String getCommand() {
            return command;
        }

        private boolean isImmediate() {
            return immediate;
        }
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
