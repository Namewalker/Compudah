package shadowlord.compudah.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import shadowlord.compudah.CompudahPlugin;

/** Enforces global visual-effect and temporary-entity limits. */
public class PerformanceManager {

    private final CompudahPlugin plugin;
    private final Map<FallingBlock, Long> temporaryBlocks = new HashMap<FallingBlock, Long>();
    private final Map<UUID, Long> fallProtection = new HashMap<UUID, Long>();
    private BukkitTask cleanupTask;
    private BukkitTask visualResetTask;
    private long tick;
    private int visualsThisTick;
    private int activeEvents;

    public PerformanceManager(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized boolean consumeVisualBudget(int amount) {
        int budget = Math.max(0, plugin.getConfig().getInt("performance.maxParticlesPerTick", 200));
        if (amount < 1 || visualsThisTick + amount > budget) {
            return false;
        }
        visualsThisTick += amount;
        if (visualResetTask == null) {
            visualResetTask = new BukkitRunnable() {
                @Override
                public void run() {
                    synchronized (PerformanceManager.this) {
                        visualsThisTick = 0;
                        visualResetTask = null;
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
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
        ensureCleanupTask();
        return true;
    }

    public synchronized void protectFromFall(Player player, int durationTicks) {
        if (player != null && durationTicks > 0) {
            fallProtection.put(player.getUniqueId(), System.currentTimeMillis() + Math.min(durationTicks, 200) * 50L);
            ensureCleanupTask();
        }
    }

    public synchronized boolean isFallProtected(UUID playerId) {
        Long expiration = fallProtection.get(playerId);
        return expiration != null && expiration > System.currentTimeMillis();
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
        Iterator<Map.Entry<UUID, Long>> iterator = fallProtection.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() <= now) {
                iterator.remove();
            }
        }
    }

    private synchronized void ensureCleanupTask() {
        if (cleanupTask != null) {
            return;
        }
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;
                cleanupExpired();
                cleanupFallProtection();
                synchronized (PerformanceManager.this) {
                    if (temporaryBlocks.isEmpty() && fallProtection.isEmpty()) {
                        cancel();
                        cleanupTask = null;
                    }
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public synchronized void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
        if (visualResetTask != null) {
            visualResetTask.cancel();
            visualResetTask = null;
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
