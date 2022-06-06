package fr.corazun.brtp.BRTPSubCommand;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface BetterRandomTeleportCommandExecutor {
    boolean onBRTPCommand(@NotNull CommandSender sender, @NotNull Command command, String subcommand, @NotNull String label, String[] args);

    List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4);
}

