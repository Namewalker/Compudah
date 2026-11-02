package shadowlord.compudah.core;

import java.io.File;
import java.net.URI;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.EventExecutor;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.IntentContext;

/** Optional, reflection-only bridge for DiscordSRV's Discord-to-game events. */
public class DiscordSrvChatBridge implements Listener {

    private static final long DEDUPLICATION_MILLIS = 5000L;
    private static final int MAX_RECENT_MESSAGES = 512;
    private static final String[] EVENT_CLASS_NAMES = {
            "github.scarsz.discordsrv.events.DiscordGuildMessagePreBroadcastEvent",
            "github.scarsz.discordsrv.events.discord.DiscordGuildMessagePreBroadcastEvent",
            "github.scarsz.discordsrv.events.DiscordGuildMessagePostBroadcastEvent",
            "github.scarsz.discordsrv.events.discord.DiscordGuildMessagePostBroadcastEvent",
            "github.scarsz.discordsrv.events.DiscordGuildMessageReceivedEvent",
            "github.scarsz.discordsrv.events.discord.DiscordGuildMessageReceivedEvent",
            "github.scarsz.discordsrv.events.DiscordGuildMessagePreProcessEvent",
            "github.scarsz.discordsrv.events.DiscordGuildMessagePostProcessEvent"
    };

    private final CompudahPlugin plugin;
    private final Map<String, Long> recentMessages = new LinkedHashMap<String, Long>();
    private Plugin discordSrv;
    private Object accountLinkManager;
    private int registeredEventTypes;

    public DiscordSrvChatBridge(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        discordSrv = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (discordSrv == null || !discordSrv.isEnabled()) {
            plugin.getLogger().info("DiscordSRV not found; in-game chat integration remains active.");
            return;
        }

        accountLinkManager = invoke(discordSrv, "getAccountLinkManager");
        for (String className : discoverEventClassNames()) {
            registerEventType(className);
        }
        if (registeredEventTypes == 0) {
            plugin.getLogger().warning("DiscordSRV is installed, but no supported Discord-to-game event class was found.");
        } else {
            plugin.getLogger().info("Registered " + registeredEventTypes + " DiscordSRV chat event hook(s).");
        }
    }

    private Set<String> discoverEventClassNames() {
        Set<String> classNames = new LinkedHashSet<String>();
        for (String className : EVENT_CLASS_NAMES) {
            classNames.add(className);
        }
        try {
            URI location = discordSrv.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            File pluginFile = new File(location);
            if (!pluginFile.isFile()) {
                return classNames;
            }
            JarFile jarFile = new JarFile(pluginFile);
            try {
                java.util.Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    String entryName = entries.nextElement().getName();
                    if (entryName.endsWith("Event.class") && entryName.contains("DiscordGuildMessage")) {
                        classNames.add(entryName.substring(0, entryName.length() - 6).replace('/', '.'));
                    }
                }
            } finally {
                jarFile.close();
            }
        } catch (Exception exception) {
            plugin.getLogger().fine("Could not inspect DiscordSRV's event classes: " + exception.getMessage());
        }
        return classNames;
    }

    @SuppressWarnings("unchecked")
    private void registerEventType(String className) {
        try {
            Class<?> eventClass = discordSrv.getClass().getClassLoader().loadClass(className);
            if (!Event.class.isAssignableFrom(eventClass)) {
                return;
            }
            Class<? extends Event> typedEvent = (Class<? extends Event>) eventClass;
            Bukkit.getPluginManager().registerEvent(typedEvent, this, EventPriority.MONITOR, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) {
                    handleDiscordEvent(event);
                }
            }, plugin, true);
            registeredEventTypes++;
        } catch (ClassNotFoundException ignored) {
            // DiscordSRV versions expose different event packages and names.
        } catch (RuntimeException exception) {
            plugin.getLogger().fine("Could not register DiscordSRV event " + className + ": " + exception.getMessage());
        }
    }

    private void handleDiscordEvent(final Event event) {
        if (event == null || isCancelled(event) || isBotMessage(event)) {
            return;
        }
        final String message = extractMessage(event);
        final String authorId = extractAuthorId(event);
        final LocalInterpreter.PreparedRequest request = message == null ? null : plugin.getLocalInterpreter().prepare(message);
        if (request == null || authorId == null
                || wasRecentlyHandled(authorId, extractMessageId(event), message)) {
            return;
        }

        final UUID linkedPlayerId = resolveLinkedPlayer(authorId);
        if (linkedPlayerId == null) {
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getResponseManager().send(null,
                            "I can only take Discord requests from a linked account. Link your Discord account first.");
                }
            });
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(linkedPlayerId);
        String linkedName = offlinePlayer.getName();
        if (linkedName == null || linkedName.trim().isEmpty()) {
            linkedName = extractAuthorName(event);
        }
        final IntentContext context = IntentContext.forDiscord(linkedPlayerId, linkedName);
        if (plugin.getMuteManager().isMuted(linkedPlayerId)) {
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.getResponseManager().send(null, "Your Discord messages are muted along with your in-game chat.");
                }
            });
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getLocalInterpreter().interpret(context, request, ChatOrigin.DISCORD);
            }
        });
    }

    private boolean isCancelled(Event event) {
        if (event instanceof org.bukkit.event.Cancellable) {
            return ((org.bukkit.event.Cancellable) event).isCancelled();
        }
        Object cancelled = invoke(event, "isCancelled");
        return Boolean.TRUE.equals(cancelled);
    }

    private boolean isBotMessage(Event event) {
        Object author = invoke(event, "getAuthor");
        Object isBot = invoke(author, "isBot");
        if (Boolean.TRUE.equals(isBot)) {
            return true;
        }
        Object webhook = invoke(event, "isWebhookMessage");
        return Boolean.TRUE.equals(webhook);
    }

    private String extractMessage(Event event) {
        Object message = invoke(event, "getMessage");
        if (message instanceof String) {
            return ((String) message).trim();
        }
        String[] contentMethods = {"getContentRaw", "getContentDisplay", "getContent", "getMessage"};
        for (String method : contentMethods) {
            Object content = invoke(message, method);
            if (content instanceof String && !((String) content).trim().isEmpty()) {
                return ((String) content).trim();
            }
        }
        Object directContent = invoke(event, "getContent");
        return directContent instanceof String ? ((String) directContent).trim() : null;
    }

    private String extractAuthorId(Event event) {
        String[] authorMethods = {"getAuthorId", "getDiscordAuthorId", "getUserId"};
        for (String method : authorMethods) {
            Object value = invoke(event, method);
            if (value != null && !value.toString().trim().isEmpty()) {
                return value.toString();
            }
        }

        Object author = invoke(event, "getAuthor");
        Object id = invoke(author, "getId");
        if (id != null) {
            return id.toString();
        }
        Object member = invoke(event, "getMember");
        Object user = invoke(member, "getUser");
        Object userId = invoke(user, "getId");
        return userId == null ? null : userId.toString();
    }

    private String extractAuthorName(Event event) {
        Object author = invoke(event, "getAuthor");
        Object name = invoke(author, "getName");
        if (name == null) {
            name = invoke(author, "getUsername");
        }
        if (name == null) {
            name = invoke(event, "getAuthorName");
        }
        return name == null ? null : name.toString();
    }

    private String extractMessageId(Event event) {
        Object message = invoke(event, "getMessage");
        Object id = invoke(message, "getId");
        if (id == null) {
            id = invoke(message, "getIdLong");
        }
        return id == null ? null : id.toString();
    }

    private UUID resolveLinkedPlayer(String discordId) {
        if (accountLinkManager == null) {
            return null;
        }
        String[] methods = {"getUuid", "getUUID", "getLinkedUuid", "getLinkedUUID"};
        for (String method : methods) {
            Object value = invoke(accountLinkManager, method, discordId);
            if (value instanceof Optional<?>) {
                value = ((Optional<?>) value).orElse(null);
            }
            if (value instanceof UUID) {
                return (UUID) value;
            }
            if (value != null) {
                try {
                    return UUID.fromString(value.toString());
                } catch (IllegalArgumentException ignored) {
                    // Try another known account-link accessor.
                }
            }
        }
        return null;
    }

    private synchronized boolean wasRecentlyHandled(String authorId, String messageId, String message) {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = recentMessages.entrySet().iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next().getValue() > DEDUPLICATION_MILLIS) {
                iterator.remove();
            }
        }
        String key = authorId + ":" + (messageId == null ? message : messageId);
        if (recentMessages.containsKey(key)) {
            return true;
        }
        recentMessages.put(key, now);
        while (recentMessages.size() > MAX_RECENT_MESSAGES) {
            recentMessages.remove(recentMessages.keySet().iterator().next());
        }
        return false;
    }

    private Object invoke(Object target, String methodName, Object... arguments) {
        if (target == null) {
            return null;
        }
        for (Method method : target.getClass().getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterTypes().length != arguments.length) {
                continue;
            }
            try {
                return method.invoke(target, arguments);
            } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
                return null;
            }
        }
        return null;
    }
}
