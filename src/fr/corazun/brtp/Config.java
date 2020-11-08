package fr.corazun.brtp;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private static Main main = JavaPlugin.getPlugin(Main.class);
    private static Configuration config;

    public static void load() {
        config = main.getConfig();
    }

    public static void reload() {
        main.saveDefaultConfig();
        main.reloadConfig();

        load();
    }

    public static Configuration get() {
        return config;
    }

    public static Integer count() {
        return get().getKeys(true).toArray().length;
    }
}
