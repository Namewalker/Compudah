package shadowlord.compudah.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import shadowlord.compudah.CompudahPlugin;

public class CompudahCommand implements CommandExecutor {

    private final CompudahPlugin plugin;

    public CompudahCommand(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            send(sender, "Help: /compudah status | /compudah reload");
            return true;
        }

        if ("status".equalsIgnoreCase(args[0])) {
            String mode = plugin.getPersonalityManager() == null ? "cheerful" : plugin.getPersonalityManager().getMode();
            send(sender, "Status: online, mode=" + mode + ", intents=" + plugin.getIntentRegistry().getIntents().size());
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("compudah.admin")) {
                send(sender, "You do not have access to Compudah administration.");
                return true;
            }
            plugin.reloadConfiguration();
            send(sender, "Compudah reloaded.");
            return true;
        }

        send(sender, "Unknown Compudah command.");
        return true;
    }

    private void send(CommandSender sender, String message) {
        plugin.getResponseManager().send(sender instanceof Player ? (Player) sender : null, message);
    }
}
