package fr.corazun.brtp;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Random;

class Utils {

    public static int setRandom(int minimalradius, int coord) {
        Random random = new Random();
        int coordonate;

        do {
            coordonate = random.nextInt(coord) - coord / 2;

        } while (coordonate <= minimalradius && coordonate >= (minimalradius * -1));

        return coordonate;
    }

    public static boolean isTeleportationSafe(Location location, Boolean isSafeTpActivated ) {
        if(!location.getBlock().getRelative(BlockFace.UP).isEmpty()) return false;

        if(location.getBlock().getRelative(BlockFace.DOWN).isEmpty()) return false;

        if(isSafeTpActivated) {
            return !location.getBlock().getRelative(BlockFace.DOWN).isLiquid();
        }

        return true;
    }
}
