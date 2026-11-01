package shadowlord.compudah.core;

import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import shadowlord.compudah.CompudahPlugin;

/** Keeps temporary visual falling blocks from changing the world. */
public class TemporaryVisualListener implements Listener {

    private final CompudahPlugin plugin;

    public TemporaryVisualListener(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock
                && plugin.getPerformanceManager().preventVisualBlockPlacement((FallingBlock) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
