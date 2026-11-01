package shadowlord.compudah.core;

import java.util.List;

/** Centralizes the rule that only a live in-game operator receives operator treatment. */
public final class ChatAuthority {

    private ChatAuthority() {
    }

    public static boolean isInGameOperator(ChatOrigin origin, boolean playerIsOp) {
        return origin == ChatOrigin.IN_GAME && playerIsOp;
    }

    public static boolean canUseIntent(ChatOrigin origin, boolean playerIsOp, String intentName, List<String> allowedIntents) {
        if (isInGameOperator(origin, playerIsOp)) {
            return true;
        }
        if (intentName == null || allowedIntents == null) {
            return false;
        }
        for (String allowed : allowedIntents) {
            if (intentName.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }
}
