package shadowlord.compudah.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EventQueueManager {

    private final JavaPlugin plugin;
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();
    private volatile boolean active;

    public EventQueueManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enqueue(Runnable runnable) {
        queue.offer(runnable);
        if (!active) {
            active = true;
            Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    Runnable next = queue.poll();
                    if (next == null) {
                        active = false;
                        return;
                    }
                    next.run();
                }
            }, 1L, 1L);
        }
    }

    public void shutdown() {
        queue.clear();
        active = false;
    }
}
