package fr.corazun.brtp.BRTPSubCommand;

import fr.corazun.brtp.Main;
import fr.corazun.brtp.Tuple;
import fr.corazun.brtp.YamlConfiguration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.SpigotConfig;

import java.util.*;
import java.util.Map.Entry;

public class BetterRandomTeleportCommandManager implements CommandExecutor, TabCompleter {
    private static final Map<String, Tuple<BetterRandomTeleportCommandExecutor, String>> SUB_COMMANDS = new HashMap<>();

    private static String bypassAllPermissions;
    private static BetterRandomTeleportCommandManager INSTANCE;

    public BetterRandomTeleportCommandManager() {
    }

    public static BetterRandomTeleportCommandManager initialize() {
        INSTANCE = new BetterRandomTeleportCommandManager();
        return INSTANCE;
    }

    public static BetterRandomTeleportCommandManager get() {
        return INSTANCE;
    }

    public BetterRandomTeleportCommandManager setBypassAllPermissions(String permission) {
        bypassAllPermissions = permission;
        return this;
    }

    public BetterRandomTeleportCommandManager add(String subCommand, BetterRandomTeleportCommandExecutor executor, String permission) {
        SUB_COMMANDS.put(subCommand.toLowerCase(), new Tuple<>(executor, permission));
        return this;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length <= 0) {
            String message = YamlConfiguration.get("messages.yml").getString("messages.plugin");

            message = StringUtils.replace(message, "%version%", Main.getPluginInstance().getDescription().getVersion());
            message = StringUtils.replace(message, "%author%", Main.getPluginInstance().getDescription().getAuthors().get(0));
            message = StringUtils.replace(message, "&", "§");

            sender.sendMessage(message);

            return true;
        } else {

            for (Entry<String, Tuple<BetterRandomTeleportCommandExecutor, String>> values : SUB_COMMANDS.entrySet()) {

                if ((values.getKey()).equals(args[0])) {
                    if (!sender.hasPermission(bypassAllPermissions) && !sender.hasPermission((values.getValue()).b())) {
                        sender.sendMessage(SpigotConfig.unknownCommandMessage);
                        return false;
                    }

                    if (((values.getValue()).a()).onBRTPCommand(sender, command, values.getKey(), label, args)) {
                        return true;
                    }
                    break;
                }
            }

            sender.sendMessage(SpigotConfig.unknownCommandMessage);
            return false;
        }
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender.hasPermission("brtp.command.*")) {
            List<String> completion = new ArrayList<>();
            if (args.length == 1) {
                SUB_COMMANDS.forEach((subCommand, tuple) -> completion.add(subCommand));
                return completion;
            }

            if (args.length > 1) {

                for (Entry<String, Tuple<BetterRandomTeleportCommandExecutor, String>> values : SUB_COMMANDS.entrySet()) {
                    if ((values.getKey()).equals(args[0])) {
                        return (values.getValue()).a().onTabComplete(commandSender, command, s, args);
                    }
                }
            }
        }

        return Collections.emptyList();
    }
}

