package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class ClearInventoryIntent extends AbstractIntent {

    public ClearInventoryIntent() {
        super("clear", Arrays.asList("clear", "clear inventory", "empty inventory", "purge inventory"), "compudah.intent.clear");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        IntentSupport.complete(player, plugin, getName(), player.getName());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = arguments == null || arguments.length == 0 ? null
                : IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "Name an online player whose inventory should be cleared.");
            return;
        }
        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
        target.updateInventory();
        IntentSupport.complete(context, plugin, getName(), target.getName());
    }
}
