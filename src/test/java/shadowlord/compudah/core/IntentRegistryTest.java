package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;
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
}
