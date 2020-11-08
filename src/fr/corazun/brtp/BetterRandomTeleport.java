package fr.corazun.brtp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BetterRandomTeleport implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 1) {

            if (args[0].equals("reload")) {

                Config.reload();

                String message = Config.get().getString("messages.reload")
                        .replace("&", "§")
                        .replace("%number%", Integer.toString(Config.count()));
                sender.sendMessage(message);

                return true;

            } else {
                String message = Config.get().getString("messages.unknown")
                        .replace("&", "§");
                sender.sendMessage(message);
            }
        }

        return false;
    }
}
