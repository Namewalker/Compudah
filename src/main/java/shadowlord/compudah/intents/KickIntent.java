package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class KickIntent extends AbstractIntent {

    public KickIntent() {
        super("kick", Arrays.asList("kick", "remove player"), "compudah.intent.kick");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
            : IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
        if (target == null || target == player) {
            plugin.getResponseManager().send(player, "Name an online player other than yourself. Self-ejection is not in my job description.");
            return;
        }
        String reason = arguments.length > 1 ? joinReason(arguments, 1) : "Removed by Compudah.";
        target.kickPlayer(reason);
        IntentSupport.complete(player, plugin, getName(), target.getName() + " / " + reason);
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
                : IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        if (target == null || target.getUniqueId().equals(context.getUniqueId())) {
            plugin.getResponseManager().send(null, "Name an online player other than yourself to kick.");
            return;
        }
        String reason = arguments.length > 1 ? joinReason(arguments, 1) : "Removed by Compudah.";
        target.kickPlayer(reason);
        IntentSupport.complete(context, plugin, getName(), target.getName() + " / " + reason);
    }

    private String joinReason(String[] args, int start) {
        StringBuilder reason = new StringBuilder();
        for (int index = start; index < args.length; index++) {
            if (reason.length() > 0) reason.append(' ');
            reason.append(args[index]);
        }
        return reason.toString();
    }

}
