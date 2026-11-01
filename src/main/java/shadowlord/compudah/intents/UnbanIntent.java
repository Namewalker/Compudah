package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class UnbanIntent extends AbstractIntent {

    public UnbanIntent() {
        super("unban", Arrays.asList("unban", "pardon"), "compudah.intent.ban");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(player, "Name the player to unban. I cannot pardon an abstract concept.");
            return;
        }
        String target = arguments[0];
        Bukkit.getBanList(BanList.Type.NAME).pardon(target);
        IntentSupport.complete(player, plugin, getName(), target);
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(null, "Name the player to unban.");
            return;
        }
        Bukkit.getBanList(BanList.Type.NAME).pardon(arguments[0]);
        IntentSupport.complete(context, plugin, getName(), arguments[0]);
    }
}
