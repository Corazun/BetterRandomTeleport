package fr.corazun.brtp;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RandomTeleport implements CommandExecutor {

    private static Main main;
    RandomTeleport(Main plugin) {
        this.main = plugin;
    }

    int x;
    int y;
    int z;

    int countFlag;
    int maxtry = 15;

    private HashMap<UUID, Long> sessions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 0) {
                if (!player.hasPermission("brtp.everywhere") && main.getConfig().getStringList("disabled-worlds").contains(player.getWorld().getName())) {
                    String message = main.getConfig().getString("messages.disabledworld");

                    message = StringUtils.replace(message,"%player%", player.getDisplayName());
                    message = StringUtils.replace(message,"%world%", player.getWorld().getName());
                    message= StringUtils.replace(message,"&", "§");

                    player.sendMessage(message);
                    return true;
                }

                int cooldown = main.getConfig().getInt("cooldown");
                UUID uuid = player.getUniqueId();

                if (!sessions.containsKey(uuid) || System.currentTimeMillis() - sessions.get(uuid) >= cooldown * 1000 || player.hasPermission("brtp.bypass")) {
                    int minimalradius = main.getConfig().getInt("min");
                    Location randomlocation;

                    countFlag = 0;

                    do {
                        y = 0;

                        if(!main.getConfig().getStringList("ignored-worlds").contains(player.getWorld().getName())) {
                            x = Utils.setRandom(minimalradius, main.getConfig().getInt("coord.x"));
                            z = Utils.setRandom(minimalradius, main.getConfig().getInt("coord.z"));
                        } else {
                            Random random = new Random();
                            x = random.nextInt(main.getConfig().getInt("coord.x")) - x / 2;
                            z = random.nextInt(main.getConfig().getInt("coord.z")) - z / 2;
                        }

                        randomlocation = new Location(player.getWorld(), x, y, z);

                        if (player.getWorld().getName().endsWith("_nether")) {
                            for (int i = 127; i > 1; i--) {
                                y = i;
                                if (randomlocation.getWorld().getBlockAt(x, y, z).isEmpty() && randomlocation.getWorld().getBlockAt(x, y, z).getRelative(BlockFace.UP).isEmpty() && !randomlocation.getWorld().getBlockAt(x, y, z).getRelative(BlockFace.DOWN).isEmpty()) {
                                    break;
                                }
                            }
                        } else {
                            y = randomlocation.getWorld().getHighestBlockYAt(randomlocation);
                        }

                        randomlocation.setY(y);

                        if(countFlag > maxtry) break;
                        countFlag++;

                    } while (!Utils.isTeleportationSafe(randomlocation, main.getConfig().getBoolean("safe-tp", true)));

                    if(countFlag <= maxtry) {
                        String message = main.getConfig().getString("messages.successtp");

                        message = StringUtils.replace(message, "%CoordX%", Integer.toString(x));
                        message = StringUtils.replace(message, "%CoordY%", Integer.toString(y));
                        message = StringUtils.replace(message, "%CoordZ%", Integer.toString(z));
                        message = StringUtils.replace(message, "&", "§");

                        player.teleport(randomlocation);
                        player.sendMessage(message);

                        Location finalRandomlocation = randomlocation;
                        main.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {
                            if (player.getLocation().getY() != y) {
                                player.teleport(finalRandomlocation);
                            }
                        }, 10L);

                        Long timestamp = System.currentTimeMillis();
                        sessions.put(uuid, timestamp);
                    } else {
                        String message = main.getConfig().getString("messages.notsafe");

                        message = StringUtils.replace(message,"%player%", player.getDisplayName());
                        message = StringUtils.replace(message,"%world%", player.getWorld().getName());
                        message= StringUtils.replace(message,"&", "§");

                        player.sendMessage(message);
                    }
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
                String message = main.getConfig().getString("messages.wrongsyntax");

                message = StringUtils.replace(message,"%player%", player.getDisplayName());
                message = StringUtils.replace(message,"&", "§");

                player.sendMessage(message);
            }
        }
        return true;
    }
}
