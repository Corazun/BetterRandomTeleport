package fr.corazun.brtp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("brtp") || command.getName().equalsIgnoreCase("betterrandomteleport")) {
            List<String> completion = new ArrayList<>();

            if (args.length == 1) {
                completion.add("reload");
                return completion;
            }

        }

        return null;
    }
}
