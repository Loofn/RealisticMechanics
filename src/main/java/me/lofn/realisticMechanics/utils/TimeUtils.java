package me.lofn.realisticMechanics.utils;

import org.bukkit.World;

public class TimeUtils {

    public static boolean isNight(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }

    public static boolean isDay(World world) {
        return !isNight(world);
    }

    public static boolean isDawn(World world) {
        long time = world.getTime();
        return time >= 22000 || time <= 1000;
    }

    public static boolean isDusk(World world) {
        long time = world.getTime();
        return time >= 12000 && time <= 13000;
    }

    public static int getMoonPhase(World world) {
        return (int) (world.getFullTime() / 24000L) % 8;
    }

    public static boolean isFullMoon(World world) {
        return getMoonPhase(world) == 0;
    }

    public static boolean isNewMoon(World world) {
        return getMoonPhase(world) == 4;
    }
}
