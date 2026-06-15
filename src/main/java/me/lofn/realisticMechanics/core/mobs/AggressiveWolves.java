package me.lofn.realisticMechanics.core.mobs;

import me.lofn.realisticMechanics.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class AggressiveWolves {

    private static final double AGGRO_CHANCE = 0.08; // 8 % chance
    private static final double RANGE = 10.0;
    private static final Set<Material> MEAT_ITEMS = Set.of(
            Material.BEEF,
            Material.PORKCHOP,
            Material.CHICKEN,
            Material.MUTTON,
            Material.RABBIT,
            Material.SALMON,
            Material.COD,
            Material.ROTTEN_FLESH
    );

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (var world : Bukkit.getWorlds()) {
                for (Wolf wolf : world.getEntitiesByClass(Wolf.class)) {
                    tryAggroWolf(wolf);
                }
            }
        }, 20L, 100L);
    }

    private static void tryAggroWolf(Wolf wolf){
        if (wolf.isTamed()) return;
        if (wolf.getTarget() != null) return;

        Player nearest = null;
        double nearestDistance = RANGE * RANGE;

        for (Player player : wolf.getWorld().getPlayers()) {
            if (!canTarget(player)) continue;

            double distance = player.getLocation().distanceSquared((wolf.getLocation()));

            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }

        if (nearest == null) return;
        double aggroChance = calculateAggroChance(nearest);

        if (Math.random() > aggroChance) return;

        wolf.setAngry(true);
        wolf.setTarget(nearest);
    }

    private static boolean canTarget(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
    }

    private static double calculateAggroChance(Player player) {

        double chance = 0.05; // base 5%

        double scent = getMeatScent(player);

        chance += Math.min(0.25, scent / 200.0);

        if (TimeUtils.isNight(player.getWorld())) {
            chance += 0.10;
        }

        return chance;
    }

    private static double getMeatScent(Player player) {

        double scent = 0;

        for (ItemStack item : player.getInventory().getStorageContents()) {

            if (item == null) continue;

            if (MEAT_ITEMS.contains(item.getType())) {

                scent += item.getAmount();
            }
        }

        return scent;
    }
}
