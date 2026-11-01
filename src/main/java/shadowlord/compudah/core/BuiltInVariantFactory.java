package shadowlord.compudah.core;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;
import shadowlord.compudah.api.IntentVariant;

/** Registers the offline, bounded behavior variants shipped with Compudah. */
public final class BuiltInVariantFactory {

    private static final List<String> KILL_VARIANTS = Arrays.asList(
            "Orbital Laser", "Precision Orbital Laser", "Meteor Strike", "Lightning Judgment",
            "Lightning Barrage", "Administrative Deletion", "Reality Correction", "Tactical Smite",
            "Divine Wrath", "Quantum Removal", "Precision Railgun", "Sudden Anvil Delivery",
            "Dragon Approved Termination", "Chaos Protocol", "Void Reassignment", "Gravity Demonstration",
            "Experimental Procedure 42", "Coin Flip Of Doom", "Emergency Maintenance", "Tactical Friendship Laser");

    private static final List<String> HEAL_VARIANTS = Arrays.asList(
            "Emergency Medical Procedure", "Aggressive Healing", "Pocket Paramedic", "Tactical Sandwich",
            "Quantum Recovery", "Divine Restoration", "Experimental Medicine", "Heart Launcher",
            "Happiness Injection", "Potion Overdose", "Authorized Wellness", "Interdimensional Ambulance",
            "Regeneration Protocol", "Vitality Reboot", "First Aid Cannon", "Emergency Blanket",
            "Medical Lightning", "Wellness Wave", "Phoenix Protocol", "The Very Competent Doctor");

    private static final List<String> TELEPORT_VARIANTS = Arrays.asList(
            "Instant Teleport", "Portal Sequence", "Countdown Warp", "Hyper-Speed Transportation",
            "Particle Trail", "Wormhole Jump", "Emergency Extraction", "Bureau Of Transportation", "Tactical Delivery");

    private BuiltInVariantFactory() {
    }

    public static void registerAll(VariantManager manager, List<Intent> intents) {
        for (Intent intent : intents) {
            String name = intent.getName();
            if ("kill".equals(name)) {
                registerNamed(manager, name, KILL_VARIANTS, "military");
            } else if ("heal".equals(name)) {
                registerNamed(manager, name, HEAL_VARIANTS, "scientific");
            } else if ("teleport".equals(name)) {
                registerNamed(manager, name, TELEPORT_VARIANTS, "transport");
            } else {
                manager.registerVariant(name, new BuiltInVariant(name + "-protocol", "bureaucratic",
                        "The {intent} protocol has been officially approved.", 0));
                manager.registerVariant(name, new BuiltInVariant(name + "-celebration", "chaotic",
                        "A completely different set of subsystems handled {intent}.", 1));
                manager.registerVariant(name, new BuiltInVariant(name + "-precision", "scientific",
                        "The precision department has reviewed {intent}.", 2));
            }
        }
    }

    private static void registerNamed(VariantManager manager, String intent, List<String> names, String family) {
        for (int index = 0; index < names.size(); index++) {
            String displayName = names.get(index);
            String id = displayName.toLowerCase(Locale.ENGLISH).replace(' ', '-');
            manager.registerVariant(intent, new BuiltInVariant(id, family,
                    displayName + " selected. {target} is receiving the full Compudah treatment.", index));
        }
    }

    private static final class BuiltInVariant implements IntentVariant {
        private final String id;
        private final String family;
        private final String message;
        private final int style;

        private BuiltInVariant(String id, String family, String message, int style) {
            this.id = id;
            this.family = family;
            this.message = message;
            this.style = style;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getFamily() {
            return family;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public void execute(final Player player, final String[] arguments, final CompudahPlugin plugin, final Intent intent) {
            if ("kill".equals(intent.getName()) && "orbital-laser".equals(id)) {
                Player target = IntentSupport.resolveTarget(player, arguments, plugin);
                if (target != null && arguments != null && arguments.length > 0) {
                    boolean queued = plugin.getDramaticEventManager().runOrbitalSequence(player, target, new Runnable() {
                        @Override
                        public void run() {
                            intent.execute(player, arguments, plugin);
                        }
                    });
                    if (queued) {
                        return;
                    }
                }
                intent.execute(player, arguments, plugin);
                return;
            }

            if ("kill".equals(intent.getName()) && style == 15) {
                Player target = IntentSupport.resolveTarget(player, arguments, plugin);
                if (target != null && arguments != null && arguments.length > 0 && plugin.getPerformanceManager().reserveEvent()) {
                    plugin.getPerformanceManager().protectFromFall(target, 100);
                    target.setVelocity(new Vector(0.0D, 0.8D, 0.0D));
                    plugin.getResponseManager().send(player, "Gravity demonstration commencing. Please enjoy the brief flight.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                if (target.isOnline() && target.isValid()) {
                                    intent.execute(player, arguments, plugin);
                                }
                            } finally {
                                plugin.getPerformanceManager().releaseEvent();
                            }
                        }
                    }.runTaskLater(plugin, 25L);
                    return;
                }
            }

            if ("teleport".equals(intent.getName()) && "countdown-warp".equals(id)
                    && scheduleCountdown(player, arguments, plugin, intent)) {
                return;
            }

            if ("teleport".equals(intent.getName()) && "hyper-speed-transportation".equals(id)
                    && scheduleHyperSpeed(player, arguments, plugin, intent)) {
                return;
            }

            Player target = resolveEffectTarget(player, arguments, plugin, intent.getName());
            Location location = target == null ? player.getLocation() : target.getLocation();
            playStyle(location, style, plugin, intent.getName(), target);
            intent.execute(player, arguments, plugin);
        }

        private boolean scheduleCountdown(final Player player, final String[] arguments, final CompudahPlugin plugin, final Intent intent) {
            if (!plugin.getPerformanceManager().reserveEvent()) {
                return false;
            }
            plugin.getResponseManager().send(player, "Coordinates locked. Beginning a completely unnecessary countdown.");
            new BukkitRunnable() {
                private int count = 3;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        plugin.getPerformanceManager().releaseEvent();
                        cancel();
                        return;
                    }
                    if (count > 0) {
                        plugin.getResponseManager().send(player, "Teleport in " + count + "...");
                        count--;
                        return;
                    }
                    try {
                        plugin.getResponseManager().send(player, "Now departing. Please keep your arms inside spacetime.");
                        intent.execute(player, arguments, plugin);
                    } finally {
                        plugin.getPerformanceManager().releaseEvent();
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 1L, 20L);
            return true;
        }

        private boolean scheduleHyperSpeed(final Player player, final String[] arguments, final CompudahPlugin plugin, final Intent intent) {
            final Location destination = resolveDestination(player, arguments, plugin);
            if (destination == null || destination.getWorld() != player.getWorld()) {
                return false;
            }
            Location start = player.getLocation();
            double distance = start.distance(destination);
            double maximumDistance = plugin.getConfig().getDouble("variants.hyper-speed-max-distance", 48.0D);
            if (distance < 1.0D || distance > maximumDistance || !routeIsLoaded(start, destination, distance)) {
                return false;
            }
            if (!plugin.getPerformanceManager().reserveEvent()) {
                return false;
            }

            final Location origin = start.clone();
            final Location end = destination.clone();
            final int steps = Math.max(2, (int) Math.ceil(distance / 2.0D));
            final int[] step = {0};
            plugin.getResponseManager().send(player, "High-speed transit engaged. Please keep all limbs inside the vehicle.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || !player.isValid()) {
                        plugin.getPerformanceManager().releaseEvent();
                        cancel();
                        return;
                    }
                    if (step[0] >= steps) {
                        try {
                            player.teleport(end);
                            player.setFallDistance(0.0F);
                            intent.execute(player, arguments, plugin);
                        } finally {
                            plugin.getPerformanceManager().releaseEvent();
                            cancel();
                        }
                        return;
                    }
                    double progress = (double) ++step[0] / steps;
                    Location point = origin.clone().add((end.getX() - origin.getX()) * progress,
                            (end.getY() - origin.getY()) * progress, (end.getZ() - origin.getZ()) * progress);
                    if (!point.getChunk().isLoaded()) {
                        plugin.getResponseManager().send(player, "Route unloaded. Transit canceled rather than forcing the world to load.");
                        plugin.getPerformanceManager().releaseEvent();
                        cancel();
                        return;
                    }
                    player.teleport(point);
                    player.setFallDistance(0.0F);
                    if (step[0] == 1 || step[0] % 5 == 0) {
                        if (plugin.getPerformanceManager().consumeVisualBudget(1)) {
                            point.getWorld().playEffect(point, Effect.FIREWORKS_SPARK, 1);
                        }
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);
            return true;
        }

        private boolean routeIsLoaded(Location start, Location end, double distance) {
            int samples = Math.max(1, (int) Math.ceil(distance / 4.0D));
            for (int index = 0; index <= samples; index++) {
                double progress = (double) index / samples;
                Location point = start.clone().add((end.getX() - start.getX()) * progress,
                        (end.getY() - start.getY()) * progress, (end.getZ() - start.getZ()) * progress);
                if (!point.getChunk().isLoaded() || !isClear(point)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isClear(Location location) {
            return !location.getBlock().getType().isSolid()
                    && !location.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType().isSolid();
        }

        private Location resolveDestination(Player player, String[] arguments, CompudahPlugin plugin) {
            if (arguments == null || arguments.length == 0) {
                return null;
            }
            int start = 0;
            if (arguments.length > 2 && arguments[0].equalsIgnoreCase("me") && arguments[1].equalsIgnoreCase("to")) {
                start = 2;
            } else if (arguments[0].equalsIgnoreCase("to")) {
                start = 1;
            }
            if (start >= arguments.length) {
                return null;
            }
            String[] destination = Arrays.copyOfRange(arguments, start, arguments.length);
            String name = IntentSupport.join(destination);
            if ("spawn".equalsIgnoreCase(name)) {
                return player.getWorld().getSpawnLocation();
            }
            Player target = IntentSupport.resolveTarget(player, destination, plugin);
            return target == null ? null : target.getLocation();
        }

        private Player resolveEffectTarget(Player player, String[] arguments, CompudahPlugin plugin, String intentName) {
            if ("kill".equals(intentName) || "heal".equals(intentName) || "feed".equals(intentName)
                    || "kick".equals(intentName) || "ban".equals(intentName) || "tempban".equals(intentName)
                    || "mute".equals(intentName) || "unmute".equals(intentName) || "bring".equals(intentName)) {
                if (arguments == null || arguments.length == 0) {
                    return player;
                }
                return IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
            }
            return player;
        }

        private void playStyle(Location location, int style, CompudahPlugin plugin, String intentName, Player target) {
            if (location == null || location.getWorld() == null || "OFF".equalsIgnoreCase(plugin.getConfig().getString("drama-level", "NORMAL"))) {
                return;
            }
            Player audience = null;
            for (Player online : location.getWorld().getPlayers()) {
                audience = online;
                break;
            }
            if (audience != null && !plugin.getPerformanceManager().consumeVisualBudget(4)) {
                return;
            }

            if ("kill".equals(intentName) && target != null && style == 2) {
                plugin.getPerformanceManager().spawnFallingBlock(location.clone().add(0.0D, 8.0D, 0.0D), Material.NETHERRACK, 40);
            } else if ("kill".equals(intentName) && target != null && style == 11) {
                plugin.getPerformanceManager().spawnFallingBlock(location.clone().add(0.0D, 8.0D, 0.0D), Material.ANVIL, 40);
            } else if ("kill".equals(intentName) && target != null && style == 15) {
                plugin.getPerformanceManager().protectFromFall(target, 100);
                target.setVelocity(new Vector(0.0D, 0.8D, 0.0D));
            }

            int pattern = style % 10;
            float pitch = 0.65F + (style % 7) * 0.18F;
            switch (pattern) {
                case 0:
                    location.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, style % 4);
                    location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_TWINKLE, 0.8F, pitch);
                    break;
                case 1:
                    location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, style % 3);
                    location.getWorld().playSound(location, Sound.ENTITY_ENDERMEN_TELEPORT, 0.8F, pitch);
                    break;
                case 2:
                    location.getWorld().strikeLightningEffect(location);
                    location.getWorld().playSound(location, Sound.ENTITY_WITHER_AMBIENT, 0.7F, pitch);
                    break;
                case 3:
                    location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);
                    location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.8F, pitch);
                    break;
                case 4:
                    location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, style % 5);
                    location.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.8F, pitch);
                    break;
                case 5:
                    location.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 1);
                    location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
                    location.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.8F, pitch);
                    break;
                case 6:
                    location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);
                    location.getWorld().strikeLightningEffect(location);
                    location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.8F, pitch);
                    break;
                case 7:
                    location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 3);
                    location.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 2);
                    location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_TWINKLE, 0.8F, pitch);
                    break;
                case 8:
                    location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 1);
                    location.getWorld().playEffect(location, Effect.EXPLOSION_HUGE, 1);
                    location.getWorld().playSound(location, Sound.ENTITY_ENDERMEN_TELEPORT, 0.8F, pitch);
                    break;
                default:
                    location.getWorld().strikeLightningEffect(location);
                    location.getWorld().playEffect(location, Effect.FIREWORKS_SPARK, 3);
                    location.getWorld().playSound(location, Sound.BLOCK_NOTE_PLING, 0.8F, pitch);
            }
            if (style >= 10) {
                Location offset = location.clone().add((style % 3) - 1, 0.5D + (style % 2), ((style / 3) % 3) - 1);
                offset.getWorld().playEffect(offset, style % 2 == 0 ? Effect.FIREWORKS_SPARK : Effect.ENDER_SIGNAL, 1);
            }
        }
    }
}
