package shadowlord.compudah.core;

import java.util.Map;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final int normalUserCooldownSeconds;
    private final int staffCooldownSeconds;
    private final Map<UUID, Long> lastUsage = new ConcurrentHashMap<UUID, Long>();
    private final Map<String, Long> lastIntentUsage = new ConcurrentHashMap<String, Long>();

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

    public boolean canUse(UUID playerId, String intentName, int cooldownSeconds) {
        if (playerId == null || intentName == null || intentName.trim().isEmpty()) {
            return false;
        }
        if (cooldownSeconds <= 0) {
            return true;
        }

        String key = playerId.toString() + ":" + intentName.toLowerCase(Locale.ENGLISH);
        long now = System.currentTimeMillis();
        Long previous = lastIntentUsage.get(key);
        if (previous != null && now - previous < cooldownSeconds * 1000L) {
            return false;
        }
        lastIntentUsage.put(key, now);
        return true;
    }

    public void clearPlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }
        lastUsage.remove(playerId);
        String prefix = playerId.toString() + ":";
        for (String key : new java.util.ArrayList<String>(lastIntentUsage.keySet())) {
            if (key.startsWith(prefix)) {
                lastIntentUsage.remove(key);
            }
        }
    }
}
