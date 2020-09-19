package fr.corazun.brtp;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("BetterRandomTeleport is now enabled !");
        this.saveDefaultConfig();
        getCommand("randomteleport").setExecutor(new RandomTeleport(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("BetterRandomTeleport is now disabled !");
    }
    
}
