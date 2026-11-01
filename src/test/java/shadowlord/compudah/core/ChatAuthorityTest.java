package shadowlord.compudah.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ChatAuthorityTest {

    @Test
    void shouldOnlyTrustOperatorStatusForDirectInGameChat() {
        assertTrue(ChatAuthority.isInGameOperator(ChatOrigin.IN_GAME, true));
        assertFalse(ChatAuthority.isInGameOperator(ChatOrigin.DISCORD, true));
        assertFalse(ChatAuthority.isInGameOperator(ChatOrigin.EXTERNAL, true));
        assertFalse(ChatAuthority.isInGameOperator(ChatOrigin.IN_GAME, false));
    }

    @Test
    void shouldApplyAllowlistToDiscordEvenForAnOperatorLinkedAccount() {
        assertTrue(ChatAuthority.canUseIntent(ChatOrigin.IN_GAME, true, "kill", Collections.<String>emptyList()));
        assertFalse(ChatAuthority.canUseIntent(ChatOrigin.DISCORD, true, "kill", Collections.<String>emptyList()));
        assertTrue(ChatAuthority.canUseIntent(ChatOrigin.DISCORD, true, "heal", Arrays.asList("heal")));
    }
}
