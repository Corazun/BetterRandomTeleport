package fr.corazun.brtp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        getLogger().info("BetterRandomTeleport is now enabled !");
        this.saveDefaultConfig();
        getCommand("randomteleport").setExecutor(new RandomTeleport(this));
        getCommand("rtp").setExecutor(new RandomTeleport(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("BetterRandomTeleport is now disabled !");
    }
}
