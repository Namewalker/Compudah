package shadowlord.compudah.api;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;

public interface DramaticEvent {

    String getName();

    void trigger(Player initiator, Player target, CompudahPlugin plugin);

    default int getDurationTicks() {
        return 40;
    }

    default boolean isEnabled() {
        return true;
    }
}
