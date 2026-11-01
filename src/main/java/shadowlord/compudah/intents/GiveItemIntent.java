package shadowlord.compudah.intents;

import java.util.Arrays;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

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
        IntentSupport.complete(player, plugin, getName(), material.name() + " x" + amount);
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        if (arguments == null || arguments.length < 2) {
            plugin.getResponseManager().send(null, "Use: give <online player> <item> [amount].");
            return;
        }
        Player target = IntentSupport.resolveTarget(context, new String[] {arguments[0]}, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "I could not find that online player.");
            return;
        }
        Material material;
        try {
            material = Material.valueOf(arguments[1].toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exception) {
            plugin.getResponseManager().send(null, "I do not recognize that item in this version of Minecraft.");
            return;
        }
        int amount = 1;
        if (arguments.length > 2) {
            try {
                amount = Integer.parseInt(arguments[2]);
            } catch (NumberFormatException exception) {
                amount = 0;
            }
        }
        if (!material.isItem() || amount < 1 || amount > 64) {
            plugin.getResponseManager().send(null, "Choose an inventory item and an amount from 1 to 64.");
            return;
        }
        target.getInventory().addItem(new ItemStack(material, amount));
        IntentSupport.complete(context, plugin, getName(), target.getName() + " / " + material.name() + " x" + amount);
    }
}
