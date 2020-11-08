package fr.corazun.brtp;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("BetterRandomTeleport is now enabled !");

        saveDefaultConfig();
        Config.load();

        getCommand("randomteleport").setExecutor(new RandomTeleport(this));
        getCommand("betterrandomteleport").setExecutor(new BetterRandomTeleport());
        getCommand("betterrandomteleport").setTabCompleter(new TabCompleter());
    }

    @Override
    public void onDisable() {
        getLogger().info("BetterRandomTeleport is now disabled !");
    }

}
