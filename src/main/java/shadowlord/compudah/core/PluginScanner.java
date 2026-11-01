package shadowlord.compudah.core;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import shadowlord.compudah.CompudahPlugin;

/** Scans installed plugins, registered commands, and permissions without server-specific classes. */
public class PluginScanner {

    private final CompudahPlugin plugin;

    public PluginScanner(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    public CapabilityRegistry scan() {
        Set<String> pluginNames = new LinkedHashSet<String>();
        Set<String> commandNames = new LinkedHashSet<String>();
        Set<String> permissionNames = new LinkedHashSet<String>();

        for (Plugin installed : Bukkit.getPluginManager().getPlugins()) {
            pluginNames.add(installed.getName());
            if (installed.isEnabled()) {
                pluginNames.add(installed.getName() + ":enabled");
            }
        }
        for (Permission permission : Bukkit.getPluginManager().getPermissions()) {
            permissionNames.add(permission.getName());
        }

        scanCommandMap(commandNames);
        if (commandNames.isEmpty()) {
            for (String alias : Bukkit.getCommandAliases().keySet()) {
                commandNames.add(alias);
            }
        }

        CapabilityRegistry registry = new CapabilityRegistry(pluginNames, commandNames, permissionNames);
        plugin.getLogger().info("Capability scan found " + registry.getPlugins().size() + " plugin entries, "
                + registry.getCommands().size() + " commands, and " + registry.getPermissions().size() + " permissions.");
        return registry;
    }

    @SuppressWarnings("unchecked")
    private void scanCommandMap(Set<String> commandNames) {
        try {
            Method getCommandMap = Bukkit.getServer().getClass().getMethod("getCommandMap");
            Object commandMap = getCommandMap.invoke(Bukkit.getServer());
            Method getKnownCommands = commandMap.getClass().getMethod("getKnownCommands");
            Object knownCommands = getKnownCommands.invoke(commandMap);
            if (knownCommands instanceof Map<?, ?>) {
                for (Object value : ((Map<?, ?>) knownCommands).values()) {
                    if (value instanceof Command) {
                        commandNames.add(((Command) value).getName());
                    }
                }
            }
        } catch (ReflectiveOperationException | SecurityException exception) {
            plugin.getLogger().fine("Command-map scanning is unavailable on this server implementation.");
        }
    }
}
