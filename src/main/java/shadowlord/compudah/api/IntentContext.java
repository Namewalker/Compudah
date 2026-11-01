package shadowlord.compudah.api;

import java.util.UUID;

import org.bukkit.entity.Player;

/** Identifies the person behind a request, whether or not they are currently online. */
public final class IntentContext {

    private final UUID uniqueId;
    private final String name;
    private final Player player;
    private final boolean inGameOperator;

    private IntentContext(UUID uniqueId, String name, Player player, boolean inGameOperator) {
        if (uniqueId == null) {
            throw new IllegalArgumentException("A linked UUID is required");
        }
        this.uniqueId = uniqueId;
        this.name = name == null || name.trim().isEmpty() ? uniqueId.toString() : name.trim();
        this.player = player;
        this.inGameOperator = inGameOperator && player != null && player.isOp();
    }

    public static IntentContext forInGame(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("A player is required for in-game chat");
        }
        return new IntentContext(player.getUniqueId(), player.getName(), player, player.isOp());
    }

    public static IntentContext forDiscord(UUID uniqueId, String linkedPlayerName) {
        return new IntentContext(uniqueId, linkedPlayerName, null, false);
    }

    public static IntentContext forDiscord(Player linkedPlayer) {
        if (linkedPlayer == null) {
            throw new IllegalArgumentException("A linked player identity is required");
        }
        return new IntentContext(linkedPlayer.getUniqueId(), linkedPlayer.getName(), linkedPlayer, false);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isOnline() {
        return player != null && player.isOnline();
    }

    public boolean isInGameOperator() {
        return inGameOperator;
    }
}
