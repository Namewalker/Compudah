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
