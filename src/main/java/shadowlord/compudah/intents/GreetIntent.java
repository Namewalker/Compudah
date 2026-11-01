package shadowlord.compudah.intents;

import java.util.Arrays;

import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;
import shadowlord.compudah.core.IntentSupport;
import shadowlord.compudah.api.IntentContext;

public class GreetIntent extends AbstractIntent {

    public GreetIntent() {
        super("greet", Arrays.asList("hello", "hi", "hey", "greet"), "compudah.use");
    }

    @Override
    public void execute(Player player, String[] arguments, CompudahPlugin plugin) {
        IntentSupport.complete(player, plugin, getName(), "social");
    }

    @Override
    protected void executeRemote(IntentContext context, String[] arguments, CompudahPlugin plugin) {
        IntentSupport.complete(context, plugin, getName(), context.getName());
    }
}
