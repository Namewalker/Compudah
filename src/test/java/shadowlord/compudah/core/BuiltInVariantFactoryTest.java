package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.api.Intent;

class BuiltInVariantFactoryTest {

    @Test
    void shouldRegisterLargeDistinctBehaviorSetsForMajorActions() {
        VariantManager manager = new VariantManager(3);
        List<Intent> intents = Arrays.<Intent>asList(new StubIntent("kill"), new StubIntent("heal"),
                new StubIntent("teleport"), new StubIntent("greet"));

        BuiltInVariantFactory.registerAll(manager, intents);

        assertEquals(20, manager.getVariants("kill").size());
        assertEquals(20, manager.getVariants("heal").size());
        assertEquals(9, manager.getVariants("teleport").size());
        assertEquals(3, manager.getVariants("greet").size());
    }

    private static final class StubIntent implements Intent {
        private final String name;

        private StubIntent(String name) {
            this.name = name;
        }

        @Override
        public String getName() { return name; }
        @Override
        public List<String> getAliases() { return Collections.singletonList(name); }
        @Override
        public String getPermission() { return "compudah.use"; }
        @Override
        public boolean isEnabled() { return true; }
        @Override
        public void execute(Player player, String[] arguments, CompudahPlugin plugin) { }
    }
}
