package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;

public abstract class AbstractIntent implements Intent {

    private final String name;
    private final List<String> aliases;
    private final String permission;
    private final boolean enabled;

    protected AbstractIntent(String name, List<String> aliases, String permission) {
        this(name, aliases, permission, true);
    }

    protected AbstractIntent(String name, List<String> aliases, String permission, boolean enabled) {
        this.name = name;
        this.aliases = aliases == null ? Arrays.asList(name) : aliases;
        this.permission = permission == null ? "compudah.use" : permission;
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    protected void requireCooldown(Player player, CompudahPlugin plugin) {
        if (player == null || plugin == null) {
            return;
        }
        if (!plugin.getCooldownManager().canUse(player.getUniqueId(), player.isOp())) {
            plugin.getResponseManager().send(player, "Compudah is still recharging. Try again in a moment.");
            throw new IllegalStateException("Cooldown active");
        }
    }
}
