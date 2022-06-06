package fr.corazun.brtp.BRTPSubCommand;

import fr.corazun.brtp.BetterRandomTeleport;
import fr.corazun.brtp.YamlConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SubCommandTeleport implements BetterRandomTeleportCommandExecutor {
    public boolean onBRTPCommand(@NotNull CommandSender sender, @NotNull Command command, String subcommand, @NotNull String label, String[] args) {
        if (args.length > 1 && args.length < 4) {
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                new BetterRandomTeleport(player, sender, args.length == 3 && Boolean.parseBoolean(args[2])).consume();
            } else {
                String message = YamlConfiguration.get("messages.yml").getString("messages.other.unknown-player");
                message = StringUtils.replace(message, "%player%", args[1]);
                message = StringUtils.replace(message, "&", "§");
                sender.sendMessage(message);
            }

            return true;
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 2) {
            return null;
        } else if (args.length == 3) {
            List<String> completer = new ArrayList<>();

            completer.add("true");
            completer.add("false");

            return completer;
        } else {
            return Collections.emptyList();
        }
    }
}
