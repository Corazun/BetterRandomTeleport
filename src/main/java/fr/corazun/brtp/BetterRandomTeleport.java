package fr.corazun.brtp;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BetterRandomTeleport {
    private static final Map<UUID, Long> SESSIONS = new HashMap<>();
    private static Map<String, Integer> PERMISSIONS = new HashMap<>();

    private static final Random RANDOM = new Random();

    private final Player player;
    private final CommandSender caster;
    private final int cooldown;
    private final World world;
    private final boolean bypass;

    public BetterRandomTeleport(Player player, CommandSender caster, boolean bypass) {
        this.caster = caster;
        this.player = player;
        this.world = player.getWorld();
        this.bypass = bypass;

        this.cooldown = PERMISSIONS.get(PERMISSIONS.keySet().stream().filter(player::hasPermission).findFirst().get());
    }

    public static void clearSession(Player player) {
        SESSIONS.remove(player.getUniqueId());
    }

    private boolean isOnCooldown() {
        UUID uuid = this.player.getUniqueId();
        return !this.player.hasPermission(Main.getLocal("PERMISSION_BYPASS")) && !this.bypass && SESSIONS.containsKey(uuid) && System.currentTimeMillis() - SESSIONS.get(uuid) < (long)this.cooldown * 1000L && !this.player.hasPermission("brtp.bypass");
    }

    private boolean canTeleportInWorld() {
        if (this.isOnCooldown()) {
            int timeleft = (int)((long)this.cooldown - (System.currentTimeMillis() - SESSIONS.get(this.player.getUniqueId())) / 1000L) - 1;
            int seconds = timeleft % 60;
            int minutes = (timeleft - seconds) / 60;
            int hours = (timeleft - seconds) / 3600;

            String message = YamlConfiguration.get("messages.yml").getString("messages.self.on-cooldown");

            message = StringUtils.replace(message, "%hours%", Integer.toString(hours));
            message = StringUtils.replace(message, "%minutes%", Integer.toString(minutes));
            message = StringUtils.replace(message, "%seconds%", Integer.toString(seconds));
            message = StringUtils.replace(message, "&", "§");

            this.player.sendMessage(message);

            if (this.caster != null) {
                message = YamlConfiguration.get("messages.yml").getString("messages.other.on-cooldown");

                message = StringUtils.replace(message, "%hours%", Integer.toString(hours));
                message = StringUtils.replace(message, "%minutes%", Integer.toString(minutes));
                message = StringUtils.replace(message, "%seconds%", Integer.toString(seconds));
                message = StringUtils.replace(message, "%player%", this.player.getName());
                message = StringUtils.replace(message, "&", "§");

                this.caster.sendMessage(message);
            }

            return false;
        } else {
            String message;

            if (!YamlConfiguration.get().getBoolean("enable-in-end") && this.world.getEnvironment() == Environment.THE_END) {
                message = YamlConfiguration.get("messages.yml").getString("messages.disabled-world");
                message = StringUtils.replace(message, "%world%", this.world.getName());
                message = StringUtils.replace(message, "&", "§");

                this.player.sendMessage(message);

                if (this.caster != null) {
                    this.caster.sendMessage(message);
                }

                return false;
            } else if (!this.player.hasPermission(Main.getLocal("PERMISSION_EVERYWHERE")) && YamlConfiguration.get().getStringList("need-permission-worlds").contains(this.world.getName()) && !this.player.hasPermission(Main.getLocal("RESTRICTED_PREFIX") + this.world.getName())) {
                message = YamlConfiguration.get("messages.yml").getString("messages.self.need-world-permission");
                message = StringUtils.replace(message, "%world%", this.world.getName());
                message = StringUtils.replace(message, "&", "§");

                this.player.sendMessage(message);

                if (this.caster != null) {
                    message = YamlConfiguration.get("messages.yml").getString("messages.other.need-world-permission");
                    message = StringUtils.replace(message, "%world%", this.world.getName());
                    message = StringUtils.replace(message, "&", "§");
                    this.caster.sendMessage(message);
                }

                return false;
            } else {
                return true;
            }
        }
    }

    private Tuple<Integer, Integer> get2DAnchor() {
        Object x = YamlConfiguration.get().get("anchor." + this.world.getName() + ".x");
        Object z = YamlConfiguration.get().get("anchor." + this.world.getName() + ".z");

        return x != null && z != null && YamlConfiguration.get().contains("anchor." + this.world.getName() + ".z") && YamlConfiguration.get().contains("anchor." + this.world.getName() + ".x") ? new Tuple<>((Integer) x, (Integer) z) : new Tuple<>(this.world.getSpawnLocation().getBlockX(), this.world.getSpawnLocation().getBlockZ());
    }

    private boolean isTeleportationSafe(Block block) {
        return block.getType() == Material.AIR && block.getRelative(BlockFace.UP).getType() == Material.AIR && !block.getRelative(BlockFace.DOWN).isEmpty() && !block.getRelative(BlockFace.DOWN).isLiquid();
    }

    private Location drawRandomLocation() {
        double maxRadius = YamlConfiguration.get().getDouble("radius." + this.world.getName() + ".max");
        double minRadius = YamlConfiguration.get().getDouble("radius." + this.world.getName() + ".min");

        if (minRadius >= maxRadius) {
            Main.getPluginInstance().getLogger().warning(Main.getLocal("LOG_MISCONFIGURED"));
            minRadius = maxRadius - 1.0D;
        }

        double spread = YamlConfiguration.get().getDouble("radius." + this.world.getName() + ".spread");

        for (int i = 0; (double)i <= YamlConfiguration.get().getDouble("brtp-allowed-retries"); ++i) {
            double boundedRadius = (double)RANDOM.nextInt((int)maxRadius - (int)minRadius) + minRadius;
            double r = boundedRadius + spread * RANDOM.nextGaussian();
            double t = Math.random() * Math.PI * 2.0D;

            Tuple<Integer, Integer> anchor = this.get2DAnchor();

            Location location = new Location(this.world, (double)Math.round((double)anchor.a() + r * Math.cos(t)) + 0.5D, 0.0D, (double)Math.round((double) anchor.b() + r * Math.sin(t)) + 0.5D);

            int blockHeight = this.world.getHighestBlockYAt(location);

            if (this.world.getEnvironment() == Environment.NETHER) {
                for(int y = 125; y >= YamlConfiguration.get().getInt("brtp-allowed-retries"); y -= 2) {
                    if (this.isTeleportationSafe(this.world.getBlockAt(location.getBlockX(), y, location.getBlockZ()))) {
                        location.setY(y);
                        return location;
                    }
                }
            } else {
                if (Double.parseDouble(Bukkit.getBukkitVersion().split("\\.")[1]) >= 13.0D) {
                    ++blockHeight;
                }

                location.setY(blockHeight);
                if (this.isTeleportationSafe(location.getBlock())) {
                    return location;
                }
            }
        }

        return null;
    }

    public void consume() {
        if (this.canTeleportInWorld()) {

            Location location = this.drawRandomLocation();
            String message;

            if (location != null) {
                this.player.teleport(location);
                Main.getPluginInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getPluginInstance(), () -> {
                    if (this.player.getLocation().getY() != (double)location.getBlockY()) {
                        this.player.teleport(location);
                    }

                }, 15L);
                
                if (!this.bypass) {
                    SESSIONS.put(this.player.getUniqueId(), System.currentTimeMillis());
                }

                message = YamlConfiguration.get("messages.yml").getString("messages.self.successful");

                message = StringUtils.replace(message, "%coordX%", Integer.toString(location.getBlockX()));
                message = StringUtils.replace(message, "%coordY%", Integer.toString(location.getBlockY()));
                message = StringUtils.replace(message, "%coordZ%", Integer.toString(location.getBlockZ()));
                message = StringUtils.replace(message, "&", "§");

                this.player.sendMessage(message);

                if (this.caster != null) {
                    message = YamlConfiguration.get("messages.yml").getString("messages.other.successful");

                    message = StringUtils.replace(message, "%coordX%", Integer.toString(location.getBlockX()));
                    message = StringUtils.replace(message, "%coordY%", Integer.toString(location.getBlockY()));
                    message = StringUtils.replace(message, "%coordZ%", Integer.toString(location.getBlockZ()));
                    message = StringUtils.replace(message, "%player%", this.player.getDisplayName());
                    message = StringUtils.replace(message, "&", "§");

                    this.caster.sendMessage(message);
                }
            } else {
                message = YamlConfiguration.get("messages.yml").getString("messages.self.not-safe");

                message = StringUtils.replace(message, "%player%", this.player.getDisplayName());
                message = StringUtils.replace(message, "%world%", this.world.getName());
                message = StringUtils.replace(message, "&", "§");

                this.player.sendMessage(message);

                if (this.caster != null) {
                    message = YamlConfiguration.get("messages.yml").getString("messages.other.not-safe");

                    message = StringUtils.replace(message, "%player%", this.player.getDisplayName());
                    message = StringUtils.replace(message, "%world%", this.world.getName());
                    message = StringUtils.replace(message, "&", "§");

                    this.caster.sendMessage(message);
                }
            }
        }

    }

    public static void reloadPermissions() {
        PERMISSIONS.clear();

        Set<String> cooldowns = YamlConfiguration.get().getConfigurationSection("cooldown").getKeys(true);

        if (!cooldowns.isEmpty()) {
            cooldowns.remove("default");

            cooldowns.forEach((permission) -> PERMISSIONS.put(Main.getLocal("COOLDOWN_PREFIX") + permission, YamlConfiguration.get().getInt("cooldown." + permission)));

            PERMISSIONS = PERMISSIONS.entrySet().stream().sorted(Entry.comparingByValue()).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        } else {
            Main.getPluginInstance().getLogger().severe(Main.getLocal("LOG_EMPTY_PERMISSIONS"));
        }

        PERMISSIONS.put(Main.getLocal("COOLDOWN_DEF_NAME"), Integer.parseInt(Main.getLocal("COOLDOWN_DEF_VALUE")));
    }
}
