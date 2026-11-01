package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;
import shadowlord.compudah.intents.HealIntent;
import shadowlord.compudah.intents.KillIntent;
import shadowlord.compudah.intents.TeleportIntent;
import shadowlord.compudah.intents.WarpIntent;

class IntentRegistryTest {

    @Test
    void shouldResolveLongestMultiWordAliasAndKeepArguments() {
        IntentRegistry registry = new IntentRegistry();
        Intent setTime = new Intent() {
            @Override
            public String getName() { return "set-time"; }
            @Override
            public java.util.List<String> getAliases() { return Arrays.asList("time", "set time"); }
            @Override
            public String getPermission() { return "compudah.intent.time"; }
            @Override
            public boolean isEnabled() { return true; }
            @Override
            public void execute(Player player, String[] arguments, CompudahPlugin plugin) { }
        };
        registry.register(setTime);

        IntentRegistry.Resolution resolution = registry.resolveCommand("set time day");

        assertEquals(setTime, resolution.getIntent());
        assertArrayEquals(new String[] {"day"}, resolution.getArguments());
    }

    @Test
    void shouldRouteWarpNamesToTheWarpIntent() {
        IntentRegistry registry = new IntentRegistry();
        registry.register(new WarpIntent());
        registry.register(new TeleportIntent());

        assertEquals("warp", registry.resolveCommand("warp market").getIntent().getName());
    }

    @Test
    void shouldResolveV2NaturalPhrasings() {
        IntentRegistry registry = new IntentRegistry();
        registry.register(new KillIntent());
        registry.register(new HealIntent());

        assertEquals("kill", registry.resolveCommand("get rid of op").getIntent().getName());
        assertEquals("op", registry.resolveCommand("get rid of op").getArguments()[0]);
        assertEquals("heal", registry.resolveCommand("i m dying").getIntent().getName());
    }
}
