package shadowlord.compudah.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import shadowlord.compudah.CompudahPlugin;

public class CompudahChatListener implements Listener {

    private final CompudahPlugin plugin;

    public CompudahChatListener(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event == null || event.isCancelled()) {
            return;
        }
        if (plugin.getMuteManager().isMuted(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            final org.bukkit.entity.Player mutedPlayer = event.getPlayer();
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getResponseManager().send(mutedPlayer, "Your words are currently on a brief administrative vacation.");
                }
            });
            return;
        }
        String message = event.getMessage();
        if (!plugin.getWakeWordDetector().containsWakeWord(message)) {
            return;
        }

        event.setCancelled(true);
        final org.bukkit.entity.Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getLocalInterpreter().interpret(player, message, ChatOrigin.IN_GAME);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        java.util.UUID playerId = event.getPlayer().getUniqueId();
        plugin.getCooldownManager().clearPlayer(playerId);
        plugin.getVariantManager().clearPlayer(playerId);
        plugin.getResponseGenerator().clearPlayer(playerId);
        plugin.getPlayerContextMemory().clear(playerId);
    }
}
