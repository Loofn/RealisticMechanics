package me.lofn.realisticMechanics.core.physics;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Set;

public class Encumbrance {

    private static final double BREATHING_THRESHOLD = 50.0;
    private static final double WATER_SINK_THRESHOLD = 80.0;
    private static final double EXHAUSTION_THRESHOLD = 120.0;
    private static final double MAX_DOWNWARD_SPEED = -1.1;

    private static final Set<Material> HEAVY_BLOCKS = Set.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.GRANITE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.TUFF,
            Material.CALCITE,
            Material.BLACKSTONE,
            Material.BASALT,
            Material.OBSIDIAN,
            Material.STONE_BRICKS,
            Material.BRICKS,
            Material.ANVIL,
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.COAL_BLOCK,
            Material.IRON_BLOCK,
            Material.GOLD_BLOCK,
            Material.DIAMOND_BLOCK,
            Material.EMERALD_BLOCK,
            Material.RAW_IRON_BLOCK,
            Material.RAW_GOLD_BLOCK,
            Material.RAW_COPPER_BLOCK
    );

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                apply(player);
            }
        }, 20L, 5L);
    }

    private static void apply(Player player) {
        if (!shouldAffect(player)) return;

        double weight = calculateWeight(player);
        double capacity = getCarryCapacity(player);
        double overload = weight - capacity;

        if (overload < BREATHING_THRESHOLD) return;

        spawnHeavyBreathingParticles(player, weight);

        if (player.isInWater() && overload >= WATER_SINK_THRESHOLD) {
            double pull = calculateDownwardPull(weight);

            Vector velocity = player.getVelocity();

            velocity.setY(Math.max(MAX_DOWNWARD_SPEED, velocity.getY() - pull));
            velocity.setX(velocity.getX() * 0.85);
            velocity.setZ(velocity.getZ() * 0.85);

            player.setVelocity(velocity);
        }

        if (overload >= EXHAUSTION_THRESHOLD) {
            applyExtraExhaustion(player, weight);
        }
    }

    private static double getCarryCapacity(Player player) {
        double capacity = BREATHING_THRESHOLD;

        if(isWearing(player, Material.GOLDEN_CHESTPLATE)) {
            capacity += 50;
        }

        return capacity;
    }

    private static boolean isWearing(Player player, Material material) {
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() == material) {
                return true;
            }
        }

        return false;
    }

    private static boolean shouldAffect(Player player) {
        if (player.isInsideVehicle()) return false;
        if (player.isFlying()) return false;

        GameMode mode = player.getGameMode();
        return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
    }

    private static void applyExtraExhaustion(Player player, double weight) {
        Vector velocity = player.getVelocity();

        boolean isMoving = Math.abs(velocity.getX()) > 0.05
                || Math.abs(velocity.getZ()) > 0.05;

        if (!isMoving) return;

        double extraExhaustion = Math.min(0.02f, weight / 5000.0);

        player.setExhaustion((float) Math.min(
                4.0f,
                player.getExhaustion() + extraExhaustion
        ));
    }

    private static double calculateDownwardPull(double weight) {
        if (weight < 80) return 0;

        double overload = weight - 80;

        return Math.min(0.12, 0.04 + (overload / 1000.0));
    }

    private static double calculateWeight(Player player) {
        double weight = 0;

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            weight += getItemWeight(armor);
        }

        for (ItemStack item : player.getInventory().getStorageContents()) {
            weight += getItemWeight(item);
        }

        return weight;
    }

    private static double getItemWeight(ItemStack item) {
        if (item == null || item.getType().isAir()) return 0;

        Material type = item.getType();
        int amount = item.getAmount();

        if (HEAVY_BLOCKS.contains(type)) {
            return 1.2 * amount;
        }

        if (Tag.SAND.isTagged(type)) {
            return 1.0 * amount;
        }

        double singleWeight = switch (type) {
            case NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS -> 18;
            case DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS -> 14;
            case IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS -> 10;

            case RAW_IRON, RAW_GOLD, RAW_COPPER,
                 COAL, IRON_INGOT, GOLD_INGOT, COPPER_INGOT,
                 DIAMOND, EMERALD -> 2.0;

            default -> 0.15;
        };

        return singleWeight * amount;
    }

    private static void spawnHeavyBreathingParticles(Player player, double weight) {
        double chance;

        if (weight < 80) {
            chance = 0.08;
        } else if (weight < 120) {
            chance = 0.18;
        } else if (weight < 180) {
            chance = 0.30;
        } else {
            chance = 0.45;
        }

        if (Math.random() > chance) return;

        Vector direction = player.getLocation().getDirection().normalize();

        Location mouth = player.getEyeLocation()
                .add(direction.multiply(0.35))
                .add(0, -0.15, 0);

        player.getWorld().spawnParticle(
                Particle.CLOUD,
                mouth,
                1,
                0.02,
                0.02,
                0.02,
                0.002
        );
    }
}
