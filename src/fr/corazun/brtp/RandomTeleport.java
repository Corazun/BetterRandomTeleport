package fr.corazun.brtp;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RandomTeleport implements CommandExecutor {

    private static Main main;
    RandomTeleport(Main plugin) { main = plugin; }

    double x, z, y;
    private HashMap<UUID, Long> sessions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            int anchorX = main.getConfig().getInt("anchor.x");
            int anchorZ = main.getConfig().getInt("anchor.z");

            if (anchorX == 0 || anchorZ == 0) {
                anchorX = player.getWorld().getSpawnLocation().getBlockX();
                anchorZ = player.getWorld().getSpawnLocation().getBlockZ();
            }

            switch(args.length) {
                case 0:
                    if (canRandomTeleport(player)) {
                        Location randomlocation;

                        for(int i = 0; ; i++) {
                            x = drawRandomCoordonate(player.getWorld().getName(), anchorX);
                            z = drawRandomCoordonate(player.getWorld().getName(), anchorZ);

                            randomlocation = new Location(player.getWorld(), x, 0, z);

                            if (player.getWorld().getName().endsWith("_nether")) {
                                for (int j = 127; j > 1; j--) {
                                    y = j;
                                    if (randomlocation.getWorld().getBlockAt((int) x, (int) y, (int) z).isEmpty() &&
                                            randomlocation.getWorld().getBlockAt((int) x, (int) y, (int) z).getRelative(BlockFace.UP).isEmpty() &&
                                                !randomlocation.getWorld().getBlockAt((int) x, (int) y, (int) z).getRelative(BlockFace.DOWN).isEmpty()) {
                                        break;
                                    }
                                }
                            } else {
                                y = randomlocation.getWorld().getHighestBlockYAt(randomlocation);

                                if (Double.parseDouble(Bukkit.getBukkitVersion().split("\\.")[1]) >= 13) { y += 1; }
                            }

                            randomlocation.setY(y);

                            if (i < 15) {
                                if (isTeleportationSafe(randomlocation, main.getConfig().getBoolean("safe-tp", true))) { break; }
                            } else {
                                String message = main.getConfig().getString("messages.notsafe");

                                message = StringUtils.replace(message,"%player%", player.getDisplayName());
                                message = StringUtils.replace(message,"%world%", player.getWorld().getName());
                                message= StringUtils.replace(message,"&", "§");

                                player.sendMessage(message);
                                return true;
                            }

                        }

                        String message = main.getConfig().getString("messages.successtp");

                        message = StringUtils.replace(message, "%CoordX%", Double.toString(x));
                        message = StringUtils.replace(message, "%CoordY%", Double.toString(y));
                        message = StringUtils.replace(message, "%CoordZ%", Double.toString(z));
                        message = StringUtils.replace(message, "&", "§");

                        player.teleport(randomlocation);
                        player.sendMessage(message);

                        Location finalRandomlocation = randomlocation;
                        main.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
                            if (player.getLocation().getY() != y) {
                                player.teleport(finalRandomlocation);
                            }
                        }, 15L);

                        Long timestamp = System.currentTimeMillis();
                        UUID uuid = player.getUniqueId();

                        sessions.put(uuid, timestamp);
                    }
                    break;
                default:
                    String message = main.getConfig().getString("messages.wrongsyntax");

                    message = StringUtils.replace(message,"%player%", player.getDisplayName());
                    message = StringUtils.replace(message,"&", "§");

                    player.sendMessage(message);
                    break;
            }

        }
        return true;
    }

    private static boolean canRandomTeleport(Player player) {
        int cooldown = main.getConfig().getInt("cooldown");

        if (player.hasPermission("brtp.everywhere") || !main.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
            UUID uuid = player.getUniqueId();

            if (!sessions.containsKey(uuid) || System.currentTimeMillis() - sessions.get(uuid) >= cooldown * 1000 || player.hasPermission("brtp.bypass")) {
                return true;

            } else {
                int timeleft = (int) (cooldown - (System.currentTimeMillis() - sessions.get(uuid)) / 1000L);
                int seconds = timeleft % 60;
                int minutes = ((timeleft - seconds) / 60);
                int hours = ((timeleft - seconds) / 3600);

                String message = main.getConfig().getString("messages.oncooldown");

                message = StringUtils.replace(message,"%hours%", Integer.toString(hours));
                message = StringUtils.replace(message,"%minutes%", Integer.toString(minutes));
                message = StringUtils.replace(message,"%seconds%", Integer.toString(seconds));
                message = StringUtils.replace(message,"&", "§");

                player.sendMessage(message);
            }
        } else {
            String message = main.getConfig().getString("messages.disabledworld");

            message = StringUtils.replace(message, "%player%", player.getDisplayName());
            message = StringUtils.replace(message, "%world%", player.getWorld().getName());
            message = StringUtils.replace(message, "&", "§");

            player.sendMessage(message);
        }

        return false;
    }

    private static boolean isTeleportationSafe(Location location, Boolean isSafeTpActivated ) {
        if(!location.getBlock().getRelative(BlockFace.UP).isEmpty()) return false;

        if(location.getBlock().getRelative(BlockFace.DOWN).isEmpty()) return false;

        if(isSafeTpActivated) {
            return !location.getBlock().getRelative(BlockFace.DOWN).isLiquid();
        }

        return true;
    }

    private static double drawRandomCoordonate(String world, int anchor) {
        Random random = new Random();

        int minCircle = main.getConfig().getInt("min-circle");
        int maxCircle = main.getConfig().getInt("max-circle");
        double coordonate;

        do {
            coordonate = random.nextInt((anchor + maxCircle) - (anchor - maxCircle)) + (anchor - maxCircle);
            if (main.getConfig().getStringList("ignored-worlds").contains(world)) { break; }
        } while (!(coordonate > (anchor + minCircle)) && !(coordonate < (anchor - minCircle)));

        return coordonate + 0.5;
    }
}
