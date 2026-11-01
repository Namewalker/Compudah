package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class BanIntent extends AbstractIntent {

    public BanIntent() {
        super("ban", Arrays.asList("ban", "ban player"), "compudah.intent.ban");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
            : IntentSupport.resolveTarget(player, new String[] {arguments[0]}, plugin);
        if (target == null || target == player) {
            plugin.getResponseManager().send(player, "Name an online player other than yourself. The ban hammer has boundaries.");
            return;
        }
        String name = target.getName();
        String reason = arguments.length > 1 ? joinReason(arguments, 1) : "Banned by Compudah.";
        Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, null, player.getName());
        target.kickPlayer("Banned: " + reason);
        plugin.getResponseManager().send(player, "Ban recorded for " + name + ". The paperwork was unusually satisfying.");
        IntentSupport.complete(player, plugin, getName(), name + " / " + reason);
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
