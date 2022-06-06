package fr.corazun.brtp.BRTPSubCommand;

import fr.corazun.brtp.BetterRandomTeleport;
import fr.corazun.brtp.YamlConfiguration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubCommandClear implements BetterRandomTeleportCommandExecutor {

    @Override
    public boolean onBRTPCommand(@NotNull CommandSender sender, @NotNull Command command, String subcommand, @NotNull String label, String[] args) {
        if (args.length == 2) {
            Player player = Bukkit.getPlayer(args[1]);

            String message;

            if (player != null) {
                BetterRandomTeleport.clearSession(player);

                message = YamlConfiguration.get("messages.yml").getString("messages.other.clear-cooldown");

                message = StringUtils.replace(message, "%player%", args[1]);
                message = StringUtils.replace(message, "&", "§");

                sender.sendMessage(message);
            } else {
                message = YamlConfiguration.get("messages.yml").getString("messages.other.unknown-player");

                message = StringUtils.replace(message, "%player%", args[1]);
                message = StringUtils.replace(message, "&", "§");

                sender.sendMessage(message);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
        return null;
    }
}
