package shadowlord.compudah.api;

import java.util.List;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;

public interface Intent {

    String getName();

    List<String> getAliases();

    String getPermission();

    boolean isEnabled();

    void execute(Player player, String[] arguments, CompudahPlugin plugin);

    default void execute(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (context == null || context.getPlayer() == null) {
            throw new UnsupportedOperationException("This intent requires an online player actor");
        }
        execute(context.getPlayer(), arguments, plugin);
    }

    default boolean matches(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase();
        for (String alias : getAliases()) {
            if (normalized.equals(alias.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
