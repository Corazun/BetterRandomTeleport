package fr.corazun.brtp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomTeleport implements CommandExecutor {
    public RandomTeleport() {
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                new BetterRandomTeleport(player, null, false).consume();
            }

            return true;
        } else {
            return false;
        }
    }
}
