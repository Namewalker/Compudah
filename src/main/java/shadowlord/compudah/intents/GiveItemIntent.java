package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class GiveItemIntent extends AbstractIntent {

    public GiveItemIntent() {
        super("give", Arrays.asList("give", "give me", "spawn item", "summon item"), "compudah.intent.give");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length == 0) {
            plugin.getResponseManager().send(player, "Name an item, and optionally an amount. Please do not request the moon; storage is limited.");
            return;
        }
        String itemName = arguments[0].toUpperCase(Locale.ENGLISH);
        int amount = 1;
        if (arguments.length > 1) {
            try {
                amount = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException exception) {
                plugin.getResponseManager().send(player, "That amount is not a number. The item dispenser is embarrassed.");
                return;
            }
        }
        if (amount < 1 || amount > 64) {
            plugin.getResponseManager().send(player, "I can give between 1 and 64 items per request. We have standards, apparently.");
            return;
        }
        Material material;
        try {
            material = Material.valueOf(itemName);
        } catch (IllegalArgumentException exception) {
            plugin.getResponseManager().send(player, "I do not recognize that item in this version of Minecraft.");
            return;
        }
        if (!material.isItem()) {
            plugin.getResponseManager().send(player, "That is not an inventory item. The laws of matter remain annoyingly strict.");
            return;
        }
        player.getInventory().addItem(new ItemStack(material, amount));
        plugin.getResponseManager().send(player, "Supply drop: " + amount + " " + material.name().toLowerCase(Locale.ENGLISH) + ". Try not to eat the packaging.");
        IntentSupport.complete(player, plugin, getName(), material.name() + " x" + amount);
    }
}
