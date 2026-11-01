package shadowlord.compudah.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import shadowlord.compudah.CompudahPlugin;

public class CompudahCommand implements CommandExecutor {

    private final CompudahPlugin plugin;

    public CompudahCommand(CompudahPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            sender.sendMessage("Compudah help: /compudah status | /compudah reload");
            return true;
        }

        if ("status".equalsIgnoreCase(args[0])) {
            String mode = plugin.getPersonalityManager() == null ? "cheerful" : plugin.getPersonalityManager().getMode();
            sender.sendMessage("Compudah status: online, mode=" + mode + ", intents=" + plugin.getIntentRegistry().getIntents().size());
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("compudah.admin")) {
                sender.sendMessage("You do not have access to Compudah administration.");
                return true;
            }
            plugin.reloadConfiguration();
            sender.sendMessage("Compudah reloaded.");
            return true;
        }

        sender.sendMessage("Unknown Compudah command.");
        return true;
    }
}
