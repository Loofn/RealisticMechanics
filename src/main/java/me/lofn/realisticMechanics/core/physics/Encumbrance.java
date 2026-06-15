package me.lofn.realisticMechanics.core.physics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Set;

import static org.apache.commons.lang3.function.Failable.apply;

public class Encumbrance {

    private static final double SINK_THRESHOLD = 35.0;
    private static final double MAX_DOWNWARD_SPEED = -0.45;

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
        if (weight < SINK_THRESHOLD) return;

        double pull = calculateDownwardPull(weight);

        Vector velocity = player.getVelocity();
        velocity.setY(Math.max(MAX_DOWNWARD_SPEED, velocity.getY() - pull));

        player.setVelocity(velocity);
    }

    private static boolean shouldAffect(Player player) {
        if (!player.isInWater()) return false;
        if (player.isInsideVehicle()) return false;
        if (player.isFlying()) return false;

        GameMode mode = player.getGameMode();
        return mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE;
    }

    private static double calculateDownwardPull(double weight) {
        return Math.min(0.035, weight / 2500.0);
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
}
