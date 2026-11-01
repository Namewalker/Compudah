package shadowlord.compudah.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final int normalUserCooldownSeconds;
    private final int staffCooldownSeconds;
    private final Map<UUID, Long> lastUsage = new ConcurrentHashMap<UUID, Long>();

    public CooldownManager(int normalUserCooldownSeconds, int staffCooldownSeconds) {
        this.normalUserCooldownSeconds = normalUserCooldownSeconds;
        this.staffCooldownSeconds = staffCooldownSeconds;
    }

    public boolean canUse(UUID playerId, boolean staff) {
        if (playerId == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        Long lastUsed = lastUsage.get(playerId);
        int cooldown = staff ? staffCooldownSeconds : normalUserCooldownSeconds;

        if (lastUsed == null) {
            lastUsage.put(playerId, currentTime);
            return true;
        }

        long elapsedSeconds = (currentTime - lastUsed) / 1000L;
        if (elapsedSeconds >= cooldown) {
            lastUsage.put(playerId, currentTime);
            return true;
        }
        return false;
    }
}
