package shadowlord.compudah.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import shadowlord.compudah.CompudahPlugin;

/** Enforces global visual-effect and temporary-entity limits. */
public class PerformanceManager {

    private final CompudahPlugin plugin;
    private final Map<FallingBlock, Long> temporaryBlocks = new HashMap<FallingBlock, Long>();
    private final Map<UUID, Long> fallProtection = new HashMap<UUID, Long>();
    private BukkitTask cleanupTask;
    private long tick;
    private int visualsThisTick;
    private int activeEvents;

    public PerformanceManager(CompudahPlugin plugin) {
        this.plugin = plugin;
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;
                visualsThisTick = 0;
                cleanupExpired();
                cleanupFallProtection();
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public synchronized boolean consumeVisualBudget(int amount) {
        int budget = Math.max(0, plugin.getConfig().getInt("performance.maxParticlesPerTick", 200));
        if (amount < 1 || visualsThisTick + amount > budget) {
            return false;
        }
        visualsThisTick += amount;
        return true;
    }

    public synchronized boolean reserveEvent() {
        int performanceLimit = plugin.getConfig().getInt("performance.maxActiveEvents",
                plugin.getConfig().getInt("performance.max-active-events", 10));
        int queueLimit = plugin.getConfig().getInt("queue.maxActiveEvents",
                plugin.getConfig().getInt("queue.max-active-events", 5));
        int limit = Math.max(0, Math.min(performanceLimit, queueLimit));
        if (activeEvents >= limit) {
            return false;
        }
        activeEvents++;
        return true;
    }

    public synchronized void releaseEvent() {
        if (activeEvents > 0) {
            activeEvents--;
        }
    }

    public synchronized boolean spawnFallingBlock(Location location, Material material, int lifetimeTicks) {
        int maximum = Math.max(0, plugin.getConfig().getInt("performance.maxTemporaryEntities", 100));
        if (location == null || location.getWorld() == null || material == null || maximum == 0
                || temporaryBlocks.size() >= maximum || !location.getChunk().isLoaded() || !consumeVisualBudget(1)) {
            return false;
        }

        FallingBlock block = location.getWorld().spawnFallingBlock(location, material, (byte) 0);
        block.setDropItem(false);
        block.setHurtEntities(false);
        temporaryBlocks.put(block, tick + Math.max(1, Math.min(100, lifetimeTicks)));
        return true;
    }

    public synchronized void protectFromFall(Player player, int durationTicks) {
        if (player != null && durationTicks > 0) {
            fallProtection.put(player.getUniqueId(), tick + Math.min(durationTicks, 200));
        }
    }

    public synchronized boolean isFallProtected(UUID playerId) {
        Long expiration = fallProtection.get(playerId);
        return expiration != null && expiration > tick;
    }

    public synchronized boolean preventVisualBlockPlacement(FallingBlock block) {
        if (block == null || !temporaryBlocks.containsKey(block)) {
            return false;
        }
        temporaryBlocks.remove(block);
        block.remove();
        return true;
    }

    private synchronized void cleanupExpired() {
        Iterator<Map.Entry<FallingBlock, Long>> iterator = temporaryBlocks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<FallingBlock, Long> entry = iterator.next();
            if (!entry.getKey().isValid() || entry.getValue() <= tick) {
                entry.getKey().remove();
                iterator.remove();
            }
        }
    }

    private synchronized void cleanupFallProtection() {
        Set<UUID> expired = new HashSet<UUID>();
        for (Map.Entry<UUID, Long> entry : fallProtection.entrySet()) {
            if (entry.getValue() <= tick) {
                expired.add(entry.getKey());
            }
        }
        for (UUID playerId : expired) {
            fallProtection.remove(playerId);
        }
    }

    public synchronized void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
        for (FallingBlock block : temporaryBlocks.keySet()) {
            if (block.isValid()) {
                block.remove();
            }
        }
        temporaryBlocks.clear();
        fallProtection.clear();
        activeEvents = 0;
    }
}
