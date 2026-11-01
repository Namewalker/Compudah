package shadowlord.compudah.core;

import java.util.ArrayDeque;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import shadowlord.compudah.CompudahPlugin;

public class EventQueueManager {

    private final JavaPlugin plugin;
    private final Queue<Runnable> queue = new ArrayDeque<Runnable>();
    private BukkitTask scheduledTask;

    public EventQueueManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized void enqueue(Runnable runnable) {
        if (runnable == null || !plugin.isEnabled()) {
            return;
        }
        if (plugin instanceof CompudahPlugin && !((CompudahPlugin) plugin).getConfig().getBoolean("queue.enabled", true)) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }

        int maximumQueued = plugin instanceof CompudahPlugin
            ? Math.max(0, ((CompudahPlugin) plugin).getConfig().getInt("queue.maxQueuedEvents",
                ((CompudahPlugin) plugin).getConfig().getInt("queue.max-queued-events", 100))) : 100;
        if (queue.size() >= maximumQueued) {
            plugin.getLogger().warning("Compudah event queue is full; dropping a dramatic event.");
            return;
        }
        queue.offer(runnable);
        if (scheduledTask == null) {
            scheduleNext();
        }
    }

    private void scheduleNext() {
        scheduledTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Runnable next;
                synchronized (EventQueueManager.this) {
                    scheduledTask = null;
                    next = queue.poll();
                }
                if (next != null) {
                    try {
                        next.run();
                    } catch (RuntimeException exception) {
                        plugin.getLogger().warning("A queued Compudah event failed: " + exception.getMessage());
                    }
                }
                synchronized (EventQueueManager.this) {
                    if (!queue.isEmpty() && scheduledTask == null) {
                        scheduleNext();
                    }
                }
            }
        }, 1L);
    }

    public synchronized void shutdown() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
        queue.clear();
    }
}
