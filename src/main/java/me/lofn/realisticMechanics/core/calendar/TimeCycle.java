package me.lofn.realisticMechanics.core.calendar;

import org.bukkit.Bukkit;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeCycle {

    private static final long DAY_END = 13000;
    private static final long FULL_DAY = 24000;

    private static final double DAY_REAL_TICKS = 45 * 60 * 20;   // 45 min
    private static final double NIGHT_REAL_TICKS = 15 * 60 * 20; // 15 min

    private static final double DAY_SPEED = DAY_END / DAY_REAL_TICKS;
    private static final double NIGHT_SPEED = (FULL_DAY - DAY_END) / NIGHT_REAL_TICKS;

    private static final Map<UUID, Double> timeBuffer = new HashMap<>();

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRules.ADVANCE_TIME, false);

                long fullTime = world.getFullTime();
                long dayTime = fullTime % FULL_DAY;

                double speed = dayTime < DAY_END ? DAY_SPEED : NIGHT_SPEED;

                double buffer = timeBuffer.getOrDefault(world.getUID(), 0.0);
                buffer += speed;

                long advance = (long) buffer;

                if (advance > 0) {
                    world.setFullTime(fullTime + advance);
                    buffer -= advance;
                }

                timeBuffer.put(world.getUID(), buffer);
            }
        }, 1L, 1L);
    }
}