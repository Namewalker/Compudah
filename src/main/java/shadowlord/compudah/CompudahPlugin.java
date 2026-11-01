package shadowlord.compudah;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import shadowlord.compudah.commands.CompudahCommand;
import shadowlord.compudah.core.CompudahChatListener;
import shadowlord.compudah.core.CooldownManager;
import shadowlord.compudah.core.DramaticEventManager;
import shadowlord.compudah.core.EventQueueManager;
import shadowlord.compudah.core.FuzzyPlayerMatcher;
import shadowlord.compudah.core.BuiltInVariantFactory;
import shadowlord.compudah.core.FallProtectionListener;
import shadowlord.compudah.core.DiscordSrvChatBridge;
import shadowlord.compudah.core.TemporaryVisualListener;
import shadowlord.compudah.core.IntentRegistry;
import shadowlord.compudah.core.LocalInterpreter;
import shadowlord.compudah.core.LoggingService;
import shadowlord.compudah.core.MuteManager;
import shadowlord.compudah.core.PersonalityManager;
import shadowlord.compudah.core.PerformanceManager;
import shadowlord.compudah.core.PluginScanner;
import shadowlord.compudah.core.CapabilityRegistry;
import shadowlord.compudah.core.PlayerContextMemory;
import shadowlord.compudah.core.ResponseManager;
import shadowlord.compudah.core.ResponseGenerator;
import shadowlord.compudah.core.WakeWordDetector;
import shadowlord.compudah.core.VariantManager;
import shadowlord.compudah.api.DramaticEvent;
import shadowlord.compudah.api.Intent;
import shadowlord.compudah.api.IntentVariant;
import shadowlord.compudah.intents.FeedIntent;
import shadowlord.compudah.intents.BanIntent;
import shadowlord.compudah.intents.BringIntent;
import shadowlord.compudah.intents.ClearInventoryIntent;
import shadowlord.compudah.intents.GameModeIntent;
import shadowlord.compudah.intents.GreetIntent;
import shadowlord.compudah.intents.GiveItemIntent;
import shadowlord.compudah.intents.HealIntent;
import shadowlord.compudah.intents.HomeIntent;
import shadowlord.compudah.intents.KickIntent;
import shadowlord.compudah.intents.KillIntent;
import shadowlord.compudah.intents.MuteIntent;
import shadowlord.compudah.intents.OnlinePlayersIntent;
import shadowlord.compudah.intents.ServerInfoIntent;
import shadowlord.compudah.intents.SetHomeIntent;
import shadowlord.compudah.intents.SetSpawnIntent;
import shadowlord.compudah.intents.SetTimeIntent;
import shadowlord.compudah.intents.SetWarpIntent;
import shadowlord.compudah.intents.SetWeatherIntent;
import shadowlord.compudah.intents.SpawnIntent;
import shadowlord.compudah.intents.StatusIntent;
import shadowlord.compudah.intents.TempbanIntent;
import shadowlord.compudah.intents.TeleportIntent;
import shadowlord.compudah.intents.TimeIntent;
import shadowlord.compudah.intents.TpsIntent;
import shadowlord.compudah.intents.UnbanIntent;
import shadowlord.compudah.intents.UnmuteIntent;
import shadowlord.compudah.intents.WeatherIntent;
import shadowlord.compudah.intents.WarpIntent;

public final class CompudahPlugin extends JavaPlugin {

    private static CompudahPlugin instance;

    private WakeWordDetector wakeWordDetector;
    private FuzzyPlayerMatcher fuzzyPlayerMatcher;
    private PlayerContextMemory playerContextMemory;
    private PersonalityManager personalityManager;
    private ResponseManager responseManager;
    private IntentRegistry intentRegistry;
    private LocalInterpreter localInterpreter;
    private DramaticEventManager dramaticEventManager;
    private EventQueueManager eventQueueManager;
    private CooldownManager cooldownManager;
    private LoggingService loggingService;
    private final MuteManager muteManager = new MuteManager();
    private VariantManager variantManager;
    private PerformanceManager performanceManager;
    private ResponseGenerator responseGenerator;
    private CapabilityRegistry capabilityRegistry;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfiguration();
        capabilityRegistry = new PluginScanner(this).scan();

        getCommand("compudah").setExecutor(new CompudahCommand(this));
        getServer().getPluginManager().registerEvents(new CompudahChatListener(this), this);
        getServer().getPluginManager().registerEvents(new FallProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new TemporaryVisualListener(this), this);
        if (getConfig().getBoolean("discordsrv.enabled", true)) {
            new DiscordSrvChatBridge(this).register();
        }

        getLogger().info("Compudah is online and ready to be dramatically unhelpful.");
    }

    @Override
    public void onDisable() {
        if (dramaticEventManager != null) {
            dramaticEventManager.shutdown();
        }
        if (eventQueueManager != null) {
            eventQueueManager.shutdown();
        }
        if (performanceManager != null) {
            performanceManager.shutdown();
        }
        getLogger().info("Compudah has gone quiet.");
    }

    public void reloadConfiguration() {
        reloadConfig();
        if (dramaticEventManager != null) dramaticEventManager.shutdown();
        if (eventQueueManager != null) eventQueueManager.shutdown();
        if (performanceManager != null) performanceManager.shutdown();

        List<String> wakeWords = new ArrayList<String>();
        wakeWords.addAll(getConfig().getStringList("wake-words"));
        wakeWords.add("compudah");
        wakeWords.add("computer");
        wakeWords.add("computah");
        wakeWords.add("compuder");
        wakeWords.add("computor");
        wakeWords.add("cumpudah");
        wakeWords.add("cumputer");
        wakeWords.add("compoota");
        wakeWords.add("compyuter");

        this.wakeWordDetector = new WakeWordDetector(wakeWords);
        this.fuzzyPlayerMatcher = new FuzzyPlayerMatcher(getConfig().getDouble("matching.minimum-confidence", 0.65d));
        this.playerContextMemory = new PlayerContextMemory(getConfig().getLong("context-expiration-seconds", 60L));
        this.personalityManager = new PersonalityManager(getConfig().getString("personality-mode", "cheerful"));
        if (getConfig().isConfigurationSection("response-pools")) {
            for (String mode : getConfig().getConfigurationSection("response-pools").getKeys(false)) {
                this.personalityManager.registerPool(mode, getConfig().getStringList("response-pools." + mode + ".default"));
            }
        }
        this.responseManager = new ResponseManager(this);
        this.cooldownManager = new CooldownManager(
                getConfig().getInt("cooldowns.normal-users", 3),
                getConfig().getInt("cooldowns.staff", 0));
        this.loggingService = new LoggingService(this);
        this.eventQueueManager = new EventQueueManager(this);
        this.dramaticEventManager = new DramaticEventManager(this, eventQueueManager);

        this.intentRegistry = new IntentRegistry();
        this.intentRegistry.register(new GreetIntent());
        this.intentRegistry.register(new StatusIntent());
        this.intentRegistry.register(new SpawnIntent());
        this.intentRegistry.register(new SetSpawnIntent());
        this.intentRegistry.register(new HomeIntent());
        this.intentRegistry.register(new SetHomeIntent());
        this.intentRegistry.register(new WarpIntent());
        this.intentRegistry.register(new SetWarpIntent());
        this.intentRegistry.register(new KillIntent());
        this.intentRegistry.register(new KickIntent());
        this.intentRegistry.register(new BanIntent());
        this.intentRegistry.register(new TempbanIntent());
        this.intentRegistry.register(new UnbanIntent());
        this.intentRegistry.register(new MuteIntent());
        this.intentRegistry.register(new UnmuteIntent());
        this.intentRegistry.register(new HealIntent());
        this.intentRegistry.register(new FeedIntent());
        this.intentRegistry.register(new GameModeIntent());
        this.intentRegistry.register(new ClearInventoryIntent());
        this.intentRegistry.register(new GiveItemIntent());
        this.intentRegistry.register(new TeleportIntent());
        this.intentRegistry.register(new BringIntent());
        this.intentRegistry.register(new OnlinePlayersIntent());
        this.intentRegistry.register(new ServerInfoIntent());
        this.intentRegistry.register(new TpsIntent());
        this.intentRegistry.register(new TimeIntent());
        this.intentRegistry.register(new SetTimeIntent());
        this.intentRegistry.register(new WeatherIntent());
        this.intentRegistry.register(new SetWeatherIntent());

        this.variantManager = new VariantManager(getConfig().getInt("variants.anti-repeat-history", 3));
        BuiltInVariantFactory.registerAll(this.variantManager, this.intentRegistry.getIntents());
        this.performanceManager = new PerformanceManager(this);
        this.responseGenerator = new ResponseGenerator(this, getConfig().getInt("responses.anti-repeat-history", 3));
        this.localInterpreter = new LocalInterpreter(this, this.intentRegistry, this.wakeWordDetector);
    }

    public static CompudahPlugin getInstance() {
        return instance;
    }

    public WakeWordDetector getWakeWordDetector() {
        return wakeWordDetector;
    }

    public FuzzyPlayerMatcher getFuzzyPlayerMatcher() {
        return fuzzyPlayerMatcher;
    }

    public PlayerContextMemory getPlayerContextMemory() {
        return playerContextMemory;
    }

    public PersonalityManager getPersonalityManager() {
        return personalityManager;
    }

    public ResponseManager getResponseManager() {
        return responseManager;
    }

    public IntentRegistry getIntentRegistry() {
        return intentRegistry;
    }

    public LocalInterpreter getLocalInterpreter() {
        return localInterpreter;
    }

    public DramaticEventManager getDramaticEventManager() {
        return dramaticEventManager;
    }

    public EventQueueManager getEventQueueManager() {
        return eventQueueManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public LoggingService getLoggingService() {
        return loggingService;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public VariantManager getVariantManager() {
        return variantManager;
    }

    public PerformanceManager getPerformanceManager() {
        return performanceManager;
    }

    public ResponseGenerator getResponseGenerator() {
        return responseGenerator;
    }

    public CapabilityRegistry getCapabilityRegistry() {
        return capabilityRegistry;
    }

    /** Registers an intent for extensions without modifying Compudah source. */
    public void registerIntent(Intent intent) {
        intentRegistry.register(intent);
    }

    /** Registers an additional behavior variant for an existing or extension intent. */
    public void registerVariant(String intentName, IntentVariant variant) {
        variantManager.registerVariant(intentName, variant);
    }

    /** Registers an extension-provided dramatic event. */
    public void registerDramaticEvent(DramaticEvent event) {
        dramaticEventManager.register(event);
    }
}
