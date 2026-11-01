package shadowlord.compudah.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.DramaticEvent;

public class DramaticEventManager {

    private final CompudahPlugin plugin;
    private final EventQueueManager queueManager;
    private final List<DramaticEvent> registeredEvents = new ArrayList<DramaticEvent>();
    private final Set<BukkitTask> activeSequences = new HashSet<BukkitTask>();

    public DramaticEventManager(CompudahPlugin plugin, EventQueueManager queueManager) {
        this.plugin = plugin;
        this.queueManager = queueManager;
    }

    public void register(DramaticEvent event) {
        if (event != null && !registeredEvents.contains(event)) {
            registeredEvents.add(event);
        }
    }

    public void trigger(Player initiator, Player target, String actionName) {
        if (initiator == null) {
            return;
        }
        if (registeredEvents.isEmpty()) {
            registerDefaultEvents();
        }

        final DramaticEvent event = chooseEvent(actionName);
        if (event != null) {
            queueManager.enqueue(new Runnable() {
                @Override
                public void run() {
                    if (target != null) {
                        event.trigger(initiator, target, plugin);
                    } else {
                        event.trigger(initiator, initiator, plugin);
                    }
                }
            });
        }
    }

    private DramaticEvent chooseEvent(String actionName) {
        if (registeredEvents.isEmpty()) {
            return null;
        }
        if (actionName == null) {
            return registeredEvents.get(0);
        }
        for (DramaticEvent event : registeredEvents) {
            if (event.getName().toLowerCase().contains(actionName.toLowerCase()) || actionName.toLowerCase().contains(event.getName().toLowerCase())) {
                return event;
            }
        }
        return registeredEvents.get(0);
    }

    private void registerDefaultEvents() {
        register(new DramaticEvent() {
            @Override
            public String getName() {
                return "orbital-laser";
            }

            @Override
            public void trigger(Player initiator, Player target, CompudahPlugin plugin) {
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.getWorld().playEffect(target.getLocation(), Effect.EXPLOSION_HUGE, 1);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
            }
        });
        register(new DramaticEvent() {
            @Override
            public String getName() {
                return "portal-transport";
            }

            @Override
            public void trigger(Player initiator, Player target, CompudahPlugin plugin) {
                target.getWorld().playEffect(target.getLocation(), Effect.ENDER_SIGNAL, 1);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            }
        });
    }

    public List<DramaticEvent> getRegisteredEvents() {
        return new ArrayList<DramaticEvent>(registeredEvents);
    }

    public boolean runOrbitalSequence(final Player initiator, final Player target, final Runnable finalAction) {
        if (initiator == null || target == null || finalAction == null || !target.isOnline()) {
            return false;
        }

        String level = plugin.getConfig().getString("drama-level", "NORMAL").toUpperCase(Locale.ENGLISH);
        if ("OFF".equals(level) || !plugin.getConfig().getBoolean("dramatic-events.orbital-laser.enabled", true)) {
            return false;
        }

        int configuredLimit = Math.min(plugin.getConfig().getInt("performance.max-active-events", 10),
                plugin.getConfig().getInt("queue.max-active-events", 5));
        if (!plugin.getConfig().getBoolean("queue.enabled", true) || configuredLimit <= activeSequences.size()) {
            return false;
        }

        int intervalTicks = Math.max(5, Math.min(40, plugin.getConfig().getInt("dramatic-events.stage-interval-ticks", 20)));
        int maxDurationTicks = Math.max(20, Math.min(200, plugin.getConfig().getInt("dramatic-events.max-duration-ticks", 100)));
        int maxStages = Math.max(2, maxDurationTicks / intervalTicks + 1);
        final int stages = Math.min("LOW".equals(level) ? 2 : ("NORMAL".equals(level) ? 4 : 5), maxStages);
        final int[] stage = {0};
        final BukkitTask[] taskReference = new BukkitTask[1];

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!target.isOnline() || !target.isValid()) {
                    initiator.sendMessage("[Compudah] Target lock lost. The orbital bureaucracy has closed the case.");
                    finishSequence(taskReference[0]);
                    cancel();
                    return;
                }

                Location location = target.getLocation();
                if (stage[0] == 0) {
                    announceNearby(target, "WARNING: ORBITAL STRIKE INCOMING. Please remain dramatically stationary.");
                    initiator.sendMessage("[Compudah] Orbital targeting online. This is absolutely not overkill.");
                    target.getWorld().playSound(location, Sound.ENTITY_WITHER_AMBIENT, 0.8F, 0.7F);
                } else if (stage[0] == stages - 1) {
                    announceNearby(target, "1... ORBITAL LANCE DEPLOYED. Please enjoy this highly unnecessary light show.");
                    target.getWorld().strikeLightningEffect(location);
                    target.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);
                    target.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 0.75F);
                    finalAction.run();
                    finishSequence(taskReference[0]);
                    cancel();
                    return;
                } else if (stage[0] == 1) {
                    announceNearby(target, "Target acquired. Checking orbital paperwork... (there is a lot of it)");
                    target.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 1);
                    target.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.8F, 1.8F);
                } else if (stage[0] == 2) {
                    announceNearby(target, "Charging friendship-adjacent beam: 3... 2...");
                    target.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.9F, 0.8F);
                } else {
                    announceNearby(target, "1... Firing the extremely expensive-looking beam!");
                    target.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
                    target.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.9F, 1.2F);
                }
                stage[0]++;
            }
        }.runTaskTimer(plugin, 1L, intervalTicks);
        taskReference[0] = task;
        activeSequences.add(task);
        return true;
    }

    private void announceNearby(Player target, String message) {
        Location center = target.getLocation();
        for (Player viewer : target.getWorld().getPlayers()) {
            if (viewer.getLocation().distanceSquared(center) <= 4096.0D) {
                viewer.sendMessage("[Compudah] " + message);
            }
        }
    }

    private void finishSequence(BukkitTask task) {
        if (task != null) {
            activeSequences.remove(task);
        }
    }

    public void shutdown() {
        for (BukkitTask task : new HashSet<BukkitTask>(activeSequences)) {
            task.cancel();
        }
        activeSequences.clear();
    }

    public void triggerDefaultFor(Player player, String message) {
        if (player == null) {
            return;
        }
        celebrate(player, message, null);
    }

    public void celebrate(Player player, String action) {
        celebrate(player, action, null);
    }

    public void celebrate(Player player, String action, String targetName) {
        if (player == null || !player.isOnline()) {
            return;
        }

        String level = plugin.getConfig().getString("drama-level", "NORMAL").toUpperCase();
        if ("OFF".equals(level)) {
            return;
        }

        Location location = player.getLocation();
        if ("LOW".equals(level)) {
            player.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.55F, 1.2F);
        } else {
            player.getWorld().playSound(location, Sound.ENTITY_FIREWORK_TWINKLE, 0.7F, 1.0F);
            player.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 1);
            if ("HIGH".equals(level) || "CHAOTIC".equals(level)) {
                player.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
                player.getWorld().strikeLightningEffect(location);
            }
            if ("CHAOTIC".equals(level)) {
                player.getWorld().playSound(location, Sound.ENTITY_ENDERMEN_TELEPORT, 0.7F, 1.5F);
            }
        }

        double responseChance = plugin.getConfig().getDouble("drama.dramatic-response-chance", 0.25D);
        double shenaniganChance = plugin.getConfig().getDouble("shenanigans.chance", 0.01D);
        if ("LOW".equals(level)) {
            responseChance *= 0.25D;
            shenaniganChance *= 0.5D;
        } else if ("HIGH".equals(level)) {
            responseChance = Math.max(responseChance, 0.65D);
            shenaniganChance *= 2.0D;
        } else if ("CHAOTIC".equals(level)) {
            responseChance = 1.0D;
            shenaniganChance *= 5.0D;
        }

        if (ThreadLocalRandom.current().nextDouble() < Math.min(1.0D, responseChance)) {
            String actionKey = action == null ? "default" : action.toLowerCase(Locale.ENGLISH);
            List<String> responses = plugin.getConfig().getStringList("drama.action-responses." + actionKey);
            if (responses.isEmpty()) {
                responses = plugin.getConfig().getStringList("drama.responses." + level.toLowerCase(Locale.ENGLISH));
            }
            if (!responses.isEmpty()) {
                player.sendMessage("[Compudah] " + responses.get(ThreadLocalRandom.current().nextInt(responses.size()))
                        .replace("{action}", action == null ? "action" : action)
                    .replace("{target}", targetName == null ? "someone" : targetName));
            }
        }

        if (plugin.getConfig().getBoolean("shenanigans.enabled", true)
                && ThreadLocalRandom.current().nextDouble() < Math.min(1.0D, shenaniganChance)) {
            List<String> names = plugin.getConfig().getStringList("shenanigans.names");
            String name = names.isEmpty() ? "Department of Questionable Decisions" : names.get(ThreadLocalRandom.current().nextInt(names.size()));
            player.sendMessage("[Compudah] Shenanigans approved by the " + name + ".");
            player.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 1);
        }
    }
}
