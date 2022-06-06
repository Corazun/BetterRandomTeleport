package fr.corazun.brtp;

import fr.corazun.brtp.BRTPSubCommand.BetterRandomTeleportCommandManager;
import fr.corazun.brtp.BRTPSubCommand.SubCommandClear;
import fr.corazun.brtp.BRTPSubCommand.SubCommandReload;
import fr.corazun.brtp.BRTPSubCommand.SubCommandTeleport;
import io.github.cdimascio.dotenv.Dotenv;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main PLUGIN_INSTANCE;
    private static Dotenv ENV;

    public void onEnable() {
        PLUGIN_INSTANCE = this;

        ENV = Dotenv.configure().directory(".").load();

        getPluginInstance().getLogger().info(getLocal("LOG_ENABLING"));

        YamlConfiguration
                .initialize()
                .loadBukkitDefault()
                .loadExternalConfig("messages.yml");

        BetterRandomTeleport.reloadPermissions();

        this.getCommand("randomteleport").setExecutor(new RandomTeleport());

        BetterRandomTeleportCommandManager
                .initialize()
                .setBypassAllPermissions(getLocal("PERMISSION_BYPASS"))
                .add("reload", new SubCommandReload(), "brtp.command.reload")
                .add("teleport", new SubCommandTeleport(), "brtp.command.teleport")
                .add("clear", new SubCommandClear(), "brtp.command.clear");

        this.getCommand("brtp").setExecutor(BetterRandomTeleportCommandManager.get());
        this.getCommand("brtp").setTabCompleter(BetterRandomTeleportCommandManager.get());

        getPluginInstance().getLogger().info(getLocal("LOG_ENABLED"));
    }

    public void onDisable() {
        this.getLogger().warning(getLocal("LOG_DISABLED"));
    }

    public static void reload() {
        getPluginInstance().getLogger().warning(getLocal("LOG_RELOADING"));

        YamlConfiguration.reload();
        BetterRandomTeleport.reloadPermissions();

        getPluginInstance().getLogger().info(getLocal("LOG_RELOAD"));
    }

    public static Main getPluginInstance() {
        return PLUGIN_INSTANCE;
    }

    public static String getLocal(String path) {
        assert ENV != null;

        return ENV.get(path);
    }

}