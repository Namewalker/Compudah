package shadowlord.compudah.core;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerContextMemory {

    private final long expirationSeconds;
    private final Map<UUID, String> lastTargetByPlayer = new ConcurrentHashMap<UUID, String>();
    private final Map<UUID, Long> lastSeenByPlayer = new ConcurrentHashMap<UUID, Long>();

    public PlayerContextMemory(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public void remember(UUID playerId, String targetName) {
        if (playerId == null || targetName == null || targetName.trim().isEmpty()) {
            return;
        }
        lastTargetByPlayer.put(playerId, targetName.trim());
        lastSeenByPlayer.put(playerId, System.currentTimeMillis());
    }

    public String resolve(UUID playerId, String fallback) {
        if (playerId == null) {
            return fallback;
        }

        String remembered = lastTargetByPlayer.get(playerId);
        Long lastSeen = lastSeenByPlayer.get(playerId);
        if (remembered == null || lastSeen == null) {
            return fallback;
        }

        long elapsedSeconds = (System.currentTimeMillis() - lastSeen) / 1000L;
        if (elapsedSeconds > expirationSeconds) {
            lastTargetByPlayer.remove(playerId);
            lastSeenByPlayer.remove(playerId);
            return fallback;
        }
        return remembered;
    }

    public String resolveReference(UUID playerId, String reference) {
        String remembered = resolve(playerId, null);
        if (remembered == null) {
            return null;
        }

        if (reference == null || reference.trim().isEmpty()) {
            return remembered;
        }

        String normalized = reference.toLowerCase(Locale.ENGLISH).trim();
        if (normalized.equals("him") || normalized.equals("her") || normalized.equals("them")
                || normalized.equals("he") || normalized.equals("she") || normalized.equals("they")
                || normalized.equals("it") || normalized.equals("that") || normalized.equals("this")) {
            return remembered;
        }

        return remembered;
    }

    public void clear(UUID playerId) {
        lastTargetByPlayer.remove(playerId);
        lastSeenByPlayer.remove(playerId);
    }
}
