package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.IntentContext;
import shadowlord.compudah.api.IntentVariant;

/** Assembles varied responses from independently configurable components. */
public class ResponseGenerator {

    private static final List<String> DEFAULT_INTROS = Arrays.asList(
            "Certainly.", "Very well.", "Understood.", "Excellent news.", "Wonderful.", "Fascinating.",
            "Curious.", "Well then.", "Good news everyone.", "Processing request.", "Acknowledged.", "Splendid.",
            "Outstanding.", "I was hoping someone would ask.", "This should be fun.", "That seems reasonable.",
            "I have several concerns.", "I am legally required to assist.", "Request accepted.", "Processing.",
            "A bold choice.", "The committee is ready.", "I have consulted the blinking lights.", "Proceeding carefully-ish.",
            "The paperwork has been located.");

    private static final List<String> DEFAULT_ENDINGS = Arrays.asList(
            "Have a nice day.", "Results may vary.", "Side effects are not my responsibility.", "Please keep all limbs inside the ride.",
            "Science compels me.", "You're welcome.", "Please try not to break anything.", "The paperwork has been completed.",
            "I hope that helped.", "That seemed important.", "The engineers will hear about this.", "This was entirely necessary.",
            "Probably.", "Don't ask questions.", "Further testing is required.", "Mission accomplished.", "Have fun.",
            "I regret nothing.", "This was approved by absolutely nobody.", "Please enjoy responsibly.", "No refunds.",
            "Please remain calm-ish.", "I have filed the appropriate forms.", "We shall never speak of this again.",
            "The universe has been notified.", "Thank you for your questionable trust.", "A satisfactory amount of drama.",
            "I will be over here, looking competent.", "This concludes the official portion.", "Please clap in an orderly fashion.");

    private static final List<String> DEFAULT_CONTENTS = Arrays.asList(
            "The {intent} request is underway for {target}.", "The relevant department has approved {intent} for {target}.",
            "A carefully excessive {intent} protocol has been initiated.", "The {intent} team is doing its very best for {target}.",
            "I have selected a needlessly elaborate method for {intent}.", "The situation involving {target} is receiving attention.",
            "The {intent} paperwork has reached the action desk.", "A small army of imaginary specialists is handling {intent}.",
            "The {intent} controls are responding to my confident button pressing.", "{target} has been included in the current operation.",
            "This {intent} has been upgraded to a full production.", "The Department of Questionable Decisions has reviewed {intent}.",
            "A practical solution to {intent} has been dressed up for the occasion.", "I have activated the {intent} subsystem and its unnecessary companion subsystem.",
            "The {intent} request is now being treated with theatrical importance.", "A highly official-looking {intent} procedure is in progress.",
            "I have asked the nearest blinking light to supervise {intent}.", "{target} is now part of a carefully managed {intent} scenario.",
            "The request has been translated into action with moderate fanfare.", "The {intent} operation has been approved by my internal committee.");

    private final CompudahPlugin plugin;
    private final RecentChoiceSelector selector;

    public ResponseGenerator(CompudahPlugin plugin, int componentHistory) {
        this.plugin = plugin;
        this.selector = new RecentChoiceSelector(componentHistory);
    }

    public String generate(Player player, String intentName, String targetName, IntentVariant variant) {
        return assemble(player == null ? null : player.getUniqueId(), intentName, targetName, variant);
    }

    public String generate(IntentContext context, String intentName, String targetName, IntentVariant variant) {
        return assemble(context == null ? null : context.getUniqueId(), intentName, targetName, variant);
    }

    private String assemble(UUID actorId, String intentName, String targetName, IntentVariant variant) {
        String mode = plugin.getPersonalityManager() == null ? "cheerful"
                : plugin.getPersonalityManager().getMode().toLowerCase(Locale.ENGLISH);
        String base = "message-components";
        List<String> intros = merge(configList(base + ".shared.intros"), configList(base + ".personalities." + mode + ".intros"));
        List<String> endings = merge(configList(base + ".shared.endings"), configList(base + ".personalities." + mode + ".endings"));
        List<String> contents = configList(base + ".contents." + intentName);
        if (contents.isEmpty()) {
            contents = configList(base + ".contents.default");
        }
        contents = merge(contents, configList(base + ".personalities." + mode + ".contents"));
        if (intros.isEmpty()) intros = DEFAULT_INTROS;
        if (endings.isEmpty()) endings = DEFAULT_ENDINGS;
        if (contents.isEmpty()) contents = DEFAULT_CONTENTS;

        String history = actorId == null ? "server" : actorId.toString();
        StringBuilder message = new StringBuilder();
        append(message, choose(history + ":intro:" + mode, intros));
        append(message, choose(history + ":content:" + intentName, contents));

        if (variant != null) {
            List<String> familyMessages = configList(base + ".families." + variant.getFamily());
            if (!familyMessages.isEmpty()) {
                append(message, choose(history + ":family:" + variant.getFamily(), familyMessages));
            }
            append(message, variant.getMessage());
        }
        append(message, choose(history + ":ending:" + mode, endings));
        return replace(message.toString(), intentName, targetName);
    }

    public String generateError(Player player, String category) {
        List<String> pool = configList("message-components.errors." + category);
        if (pool.isEmpty()) {
            if ("permission".equals(category)) {
                pool = Arrays.asList("I could do that. I just don't trust you enough.", "Additional authority required.",
                        "You appear to lack the necessary paperwork.");
            } else if ("target".equals(category)) {
                pool = Arrays.asList("Who?", "I looked everywhere. They aren't here.",
                        "The requested lifeform could not be located.");
            } else if ("cooldown".equals(category)) {
                pool = Arrays.asList("My cooldown is still ticking. Even I need a dramatic pause.",
                        "One moment. The subsystems are catching their breath.");
            } else {
                pool = Arrays.asList("Umm... I don't understand what you mean.",
                        "My tiny digital brain cannot comprehend this request.",
                        "That sounded important. Unfortunately I have no idea what it means.",
                        "I attempted to understand. The attempt was unsuccessful.");
            }
        }
        String identity = player == null ? "server" : player.getUniqueId().toString();
        return choose(identity + ":error:" + category, pool);
    }

    public void clearPlayer(UUID playerId) {
        if (playerId != null) {
            selector.clearPrefix(playerId.toString() + ":");
        }
    }

    private List<String> configList(String path) {
        List<String> configured = plugin.getConfig().getStringList(path);
        return configured == null ? Collections.<String>emptyList() : configured;
    }

    private List<String> merge(List<String> first, List<String> second) {
        List<String> combined = new ArrayList<String>(first);
        combined.addAll(second);
        return combined;
    }

    private String choose(String key, List<String> pool) {
        return selector.choose(key, pool);
    }

    private void append(StringBuilder message, String component) {
        if (component != null && !component.trim().isEmpty()) {
            if (message.length() > 0) message.append(' ');
            message.append(component.trim());
        }
    }

    private String replace(String message, String intentName, String targetName) {
        String target = targetName == null || targetName.trim().isEmpty() ? "the request" : targetName;
        return message.replace("{intent}", humanize(intentName)).replace("{target}", target);
    }

    private String humanize(String value) {
        return value == null ? "request" : value.replace('-', ' ');
    }
}
