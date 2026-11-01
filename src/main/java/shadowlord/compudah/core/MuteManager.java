package shadowlord.compudah.core;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MuteManager {

    private final Set<UUID> mutedPlayers = ConcurrentHashMap.newKeySet();

    public boolean mute(UUID playerId) {
        return playerId != null && mutedPlayers.add(playerId);
    }

    public boolean unmute(UUID playerId) {
        return playerId != null && mutedPlayers.remove(playerId);
    }

    public boolean isMuted(UUID playerId) {
        return playerId != null && mutedPlayers.contains(playerId);
    }
}
