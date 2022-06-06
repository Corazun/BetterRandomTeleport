package fr.corazun.brtp;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class YamlConfiguration {
    private static final Map<String, Tuple<File, FileConfiguration>> YML_CONFIGURATIONS = new HashMap<>();

    YamlConfiguration() {
    }

    public static YamlConfiguration initialize() {
        return new YamlConfiguration();
    }

    public YamlConfiguration loadBukkitDefault() {
        Main.getPluginInstance().saveDefaultConfig();
        return this;
    }

    public YamlConfiguration loadExternalConfig(String configFilePath) {
        File configFile = new File(Main.getPluginInstance().getDataFolder().getPath() + File.separator + configFilePath);

        if (!configFile.exists()) {
            Main.getPluginInstance().saveResource(configFilePath, false);
        }

        YML_CONFIGURATIONS.put(configFilePath, new Tuple<>(configFile, org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(configFile)));
        return this;
    }

    private static void reloadExternalConfig(File file) {
        FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);

        InputStream defExternalConfigStream = Main.getPluginInstance().getResource(file.getName());
        if (defExternalConfigStream != null) {
            config.setDefaults(org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new InputStreamReader(defExternalConfigStream, Charsets.UTF_8)));
        }

        YML_CONFIGURATIONS.replace(file.getName(), new Tuple<>(file, config));
    }

    private static void saveDefaultExternalConfig(File file) {
        if (!file.exists()) {
            Main.getPluginInstance().saveResource(file.getName(), false);
        }
    }

    public static void reload() {
        Main.getPluginInstance().saveDefaultConfig();
        Main.getPluginInstance().reloadConfig();

        YML_CONFIGURATIONS.forEach((k, v) -> {
            saveDefaultExternalConfig(v.a());
            reloadExternalConfig(v.a());
        });
    }

    @NotNull
    public static Configuration get() {
        return Main.getPluginInstance().getConfig();
    }

    @NotNull
    public static FileConfiguration get(String configFilePath) {
        return YML_CONFIGURATIONS.get(configFilePath).b();
    }

    public static Integer keys() {
        return get().getKeys(true).toArray().length;
    }

    public static Integer keys(String configFilePath) {
        return get(configFilePath).getKeys(true).toArray().length;
    }

    public static Integer files() {
        return YML_CONFIGURATIONS.size() + 1;
    }
}
