package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CooldownManagerTest {

    @Test
    void shouldTrackCooldownsIndependentlyPerIntent() {
        CooldownManager manager = new CooldownManager(3, 0);
        UUID playerId = UUID.randomUUID();

        assertTrue(manager.canUse(playerId, "heal", 60));
        assertFalse(manager.canUse(playerId, "heal", 60));
        assertTrue(manager.canUse(playerId, "feed", 60));
    }
}
