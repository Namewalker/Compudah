package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class PlayerContextMemoryTest {

    @Test
    void shouldResolvePronounsUsingLastKnownTarget() {
        PlayerContextMemory memory = new PlayerContextMemory(60L);
        UUID playerId = UUID.randomUUID();

        memory.remember(playerId, "op_clutcher");

        assertEquals("op_clutcher", memory.resolveReference(playerId, "him"));
        assertEquals("op_clutcher", memory.resolveReference(playerId, "her"));
        assertEquals("op_clutcher", memory.resolveReference(playerId, "them"));
    }
}
