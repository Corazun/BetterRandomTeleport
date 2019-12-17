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

    private Main main;
    RandomTeleport(Main plugin) {
        this.main = plugin;
    }

    private HashMap<UUID, Long> sessions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 0) {
                int cooldown = main.getConfig().getInt("cooldown");
                UUID uuid = player.getUniqueId();

                if (!sessions.containsKey(uuid) || System.currentTimeMillis() - sessions.get(uuid) >= cooldown * 1000 || player.hasPermission("brtp.bypass")) {
                    int minimalradius = main.getConfig().getInt("min");
                    Location randomlocation;
                    int x;
                    int y;
                    int z;

                    while (true) {
                        x = Utils.setRandom(minimalradius, main.getConfig().getInt("coord.x"));
                        y = 0;
                        z = Utils.setRandom(minimalradius, main.getConfig().getInt("coord.z"));

                        randomlocation = new Location(player.getWorld(), x, y, z);
                        y = randomlocation.getWorld().getHighestBlockYAt(randomlocation);
                        randomlocation.setY(y);

                        if(main.getConfig().getBoolean("safe-tp", true)) {
                            boolean block = randomlocation.getBlock().getRelative(BlockFace.DOWN).isLiquid();
                            
                            if (!block) {
                                break;
                            }
                        }
                        else {
                            break;
                        }
                    }

                    String message = main.getConfig().getString("messages.successtp");

                    message = StringUtils.replace(message, "%CoordX%", Integer.toString(x));
                    message = StringUtils.replace(message,"%CoordY%", Integer.toString(y));
                    message = StringUtils.replace(message,"%CoordZ%", Integer.toString(z));
                    message= StringUtils.replace(message,"&", "§");

                    player.teleport(randomlocation);
                    player.sendMessage(message);

                    Long timestamp = System.currentTimeMillis();
                    sessions.put(uuid, timestamp);
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
            }
            else {
                String message = main.getConfig().getString("messages.wrongsyntax");
                message = StringUtils.replace(message,"%player%", player.getDisplayName());
                message = StringUtils.replace(message,"&", "§");

                player.sendMessage(message);
            }
        }
        return false;
    }
}
