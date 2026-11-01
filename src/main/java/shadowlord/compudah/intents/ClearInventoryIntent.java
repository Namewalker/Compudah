package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class ClearInventoryIntent extends AbstractIntent {

    public ClearInventoryIntent() {
        super("clear", Arrays.asList("clear", "clear inventory", "empty inventory", "purge inventory"), "compudah.intent.clear");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        plugin.getResponseManager().send(player, "Inventory cleared. Your pockets are now emotionally available.");
        IntentSupport.complete(player, plugin, getName(), player.getName());
    }
}
