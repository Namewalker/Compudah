package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;

public class GreetIntent extends AbstractIntent {

    public GreetIntent() {
        super("greet", Arrays.asList("hello", "hi", "hey", "greet"), "compudah.use");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        String greeting = plugin.getPersonalityManager().pickResponse("greet", "Hello, human.");
        plugin.getResponseManager().send(player, greeting + " I have been waiting for someone interesting to appear.");
        IntentSupport.complete(player, plugin, getName(), "social");
    }
}
