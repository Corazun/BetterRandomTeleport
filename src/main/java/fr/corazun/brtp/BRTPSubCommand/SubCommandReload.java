package fr.corazun.brtp.BRTPSubCommand;

import fr.corazun.brtp.Main;
import fr.corazun.brtp.YamlConfiguration;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SubCommandReload implements BetterRandomTeleportCommandExecutor {
    public boolean onBRTPCommand(@NotNull CommandSender sender, @NotNull Command command, String subcommand, @NotNull String label, String[] args) {
        String message = YamlConfiguration.get("messages.yml").getString("messages.reloading").replace("&", "§").replace("%files%", Integer.toString(YamlConfiguration.files()));
        sender.sendMessage(message);

        Main.reload();

        String warn = YamlConfiguration.get("messages.yml").getString("messages.reloaded").replace("&", "§").replace("%keys%", Integer.toString(YamlConfiguration.keys() + YamlConfiguration.keys("messages.yml"))).replace("%files%", Integer.toString(YamlConfiguration.files()));
        sender.sendMessage(warn);

        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}
