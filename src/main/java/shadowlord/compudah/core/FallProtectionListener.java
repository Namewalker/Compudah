package shadowlord.compudah.core;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import shadowlord.compudah.CompudahPlugin;

/** Cancels fall damage caused by Compudah's temporary launch effects. */
public class FallProtectionListener implements Listener {

    private final CompudahPlugin plugin;

    public FallProtectionListener(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL || !(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (plugin.getPerformanceManager().isFallProtected(player.getUniqueId())) {
            event.setCancelled(true);
            player.setFallDistance(0.0F);
        }
    }
}
