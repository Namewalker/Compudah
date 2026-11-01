package shadowlord.compudah.core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/** Immutable snapshot of capabilities discovered on the running server. */
public class CapabilityRegistry {

    private final Set<String> plugins;
    private final Set<String> commands;
    private final Set<String> permissions;

    public CapabilityRegistry(Set<String> plugins, Set<String> commands, Set<String> permissions) {
        this.plugins = immutableNormalized(plugins);
        this.commands = immutableNormalized(commands);
        this.permissions = immutableNormalized(permissions);
    }

    private Set<String> immutableNormalized(Set<String> values) {
        Set<String> normalized = new LinkedHashSet<String>();
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    normalized.add(value.toLowerCase(Locale.ENGLISH));
                }
            }
        }
        return Collections.unmodifiableSet(normalized);
    }

    public Set<String> getPlugins() {
        return plugins;
    }

    public Set<String> getCommands() {
        return commands;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean hasPlugin(String name) {
        return name != null && plugins.contains(name.toLowerCase(Locale.ENGLISH));
    }

    public boolean hasCommand(String name) {
        return name != null && commands.contains(name.toLowerCase(Locale.ENGLISH));
    }

    public boolean hasPermission(String name) {
        return name != null && permissions.contains(name.toLowerCase(Locale.ENGLISH));
    }
}
