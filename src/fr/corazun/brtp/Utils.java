package fr.corazun.brtp;

import java.util.Random;

class Utils {

    static int setRandom(int minimalradius, int coord) {
        Random random = new Random();
        int coordonate;

        do {
            coordonate = random.nextInt(coord) - coord / 2;

        } while (coordonate <= minimalradius && coordonate >= (minimalradius * -1));

        return coordonate;
    }
}
