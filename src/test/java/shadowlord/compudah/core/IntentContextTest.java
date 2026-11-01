package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import shadowlord.compudah.api.IntentContext;

class IntentContextTest {

    @Test
    void shouldRepresentLinkedDiscordUsersWithoutAnOnlinePlayer() {
        UUID accountId = UUID.randomUUID();

        IntentContext context = IntentContext.forDiscord(accountId, "RemotePlayer");

        assertEquals(accountId, context.getUniqueId());
        assertEquals("RemotePlayer", context.getName());
        assertNull(context.getPlayer());
        assertFalse(context.isInGameOperator());
    }
}
