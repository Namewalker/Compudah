package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class HealIntent extends AbstractIntent {

    public HealIntent() {
        super("heal", Arrays.asList("heal", "restore", "mend", "patch", "fix", "help", "medical assistance",
            "save me", "i m dying", "make me not dead"), "compudah.intent.heal");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        Player target = IntentSupport.resolveTarget(player, arguments, plugin);

        if (target == null) {
            plugin.getResponseManager().send(player, "I have absolutely no idea who you're talking about.");
            return;
        }

        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setFireTicks(0);
        target.removePotionEffect(PotionEffectType.POISON);
        target.removePotionEffect(PotionEffectType.WITHER);
        plugin.getPlayerContextMemory().remember(player.getUniqueId(), target.getName());
        IntentSupport.complete(player, plugin, getName(), target.getName());
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        Player target = IntentSupport.resolveTarget(context, arguments, plugin);
        if (target == null) {
            plugin.getResponseManager().send(null, "Name an online player to heal. I cannot reach your offline character's health bar.");
            return;
        }
        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setFireTicks(0);
        target.removePotionEffect(PotionEffectType.POISON);
        target.removePotionEffect(PotionEffectType.WITHER);
        plugin.getPlayerContextMemory().remember(context.getUniqueId(), target.getName());
        IntentSupport.complete(context, plugin, getName(), target.getName());
    }
}
