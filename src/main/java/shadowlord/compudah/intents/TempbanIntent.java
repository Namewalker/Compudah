package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Date;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class TempbanIntent extends AbstractIntent {

    public TempbanIntent() {
        super("tempban", Arrays.asList("tempban", "temporary ban"), "compudah.intent.ban");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length < 2) {
            plugin.getResponseManager().send(player, "Use: tempban <player> <minutes> [reason]. The calendar requires a number.");
            return;
        }
        Player target = IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
        long minutes;
        try {
            minutes = Long.parseLong(arguments[1]);
        } catch (NumberFormatException exception) {
            minutes = 0L;
        }
        if (target == null || target == player || minutes < 1L || minutes > 525600L) {
            plugin.getResponseManager().send(player, "That player or duration is invalid. Duration must be 1 to 525600 minutes.");
            return;
        }
        String reason = arguments.length > 2 ? joinReason(arguments, 2) : "Temporarily banned by Compudah.";
        Date expires = new Date(System.currentTimeMillis() + minutes * 60000L);
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, expires, player.getName());
        target.kickPlayer("Temporarily banned for " + minutes + " minutes: " + reason);
        plugin.getResponseManager().send(player, "Temporary ban set for " + target.getName() + " (" + minutes + " minutes). A very punctual consequence.");
        IntentSupport.complete(player, plugin, getName(), target.getName() + " / " + minutes + "m");
    }

    private String joinReason(String[] args, int start) {
        StringBuilder reason = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (reason.length() > 0) reason.append(' ');
            reason.append(args[i]);
        }
        return reason.toString();
    }
}
