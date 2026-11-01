package shadowlord.compudah.api;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;

/** A behaviorally distinct execution strategy for an intent. */
public interface IntentVariant {

    /** Returns the stable, intent-local variant identifier. */
    String getId();

    /** Returns the event family used to assemble a fitting response. */
    String getFamily();

    /** Returns the variant-specific line included in the response. */
    String getMessage();

    /** Executes this behavior, normally delegating the requested action to the intent. */
    void execute(Player player, String[] arguments, CompudahPlugin plugin, Intent intent);

    default void execute(IntentContext context, String[] arguments, CompudahPlugin plugin, Intent intent) {
        if (context.getPlayer() != null) {
            execute(context.getPlayer(), arguments, plugin, intent);
        } else {
            intent.execute(context, arguments, plugin);
        }
    }
}
