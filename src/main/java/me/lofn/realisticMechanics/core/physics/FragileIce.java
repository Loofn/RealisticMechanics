package me.lofn.realisticMechanics.core.physics;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FragileIce {

    private static final double WARNING_CHANCE = 0.20;
    private static final double BREAK_CHANCE = 0.08;

    private static final Set<UUID> warnedPlayers = new HashSet<>();

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkPlayer(plugin, player);
            }
        }, 20L, 10L);
    }

    private static void checkPlayer(JavaPlugin plugin, Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) return;
        if (player.isFlying() || player.isInsideVehicle()) return;

        Block blockBelow = player.getLocation().subtract(0, 0.1, 0).getBlock();

        if (!isFragileIce(blockBelow)) {
            warnedPlayers.remove(player.getUniqueId());
            return;
        }

        if (!warnedPlayers.contains(player.getUniqueId()) && Math.random() < WARNING_CHANCE) {
            warnedPlayers.add(player.getUniqueId());

            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_GLASS_HIT,
                    0.8f,
                    0.6f
            );

            player.getWorld().spawnParticle(
                    Particle.BLOCK,
                    blockBelow.getLocation().add(0.5, 1.0, 0.5),
                    8,
                    blockBelow.getBlockData()
            );

            return;
        }

        if (warnedPlayers.contains(player.getUniqueId()) && Math.random() < BREAK_CHANCE) {
            warnedPlayers.remove(player.getUniqueId());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!isFragileIce(blockBelow)) return;

               breakIceArea(blockBelow);

                blockBelow.getWorld().playSound(
                        blockBelow.getLocation(),
                        Sound.BLOCK_GLASS_BREAK,
                        1.0f,
                        0.7f
                );

                blockBelow.getWorld().spawnParticle(
                        Particle.BLOCK,
                        blockBelow.getLocation().add(0.5, 0.5, 0.5),
                        20,
                        blockBelow.getBlockData()
                );
            }, 10L);
        }
    }

    private static boolean isFragileIce(Block block) {
        Material type = block.getType();

        return type == Material.ICE
                || type == Material.FROSTED_ICE
                || type == Material.PACKED_ICE;
    }

    private static void breakIceArea(Block center) {
        World world = center.getWorld();

        int radius = Math.random() < 0.4 ? 2 : 1; // sometimes bigger hole

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if (Math.abs(x) + Math.abs(z) > radius + 1) continue;

                Block block = center.getRelative(x, 0, z);

                if (!isFragileIce(block)) continue;

                block.setType(Material.WATER);

                world.spawnParticle(
                        Particle.BLOCK,
                        block.getLocation().add(0.5, 0.5, 0.5),
                        10,
                        block.getBlockData()
                );
            }
        }

        world.playSound(
                center.getLocation(),
                Sound.BLOCK_GLASS_BREAK,
                1.2f,
                0.65f
        );
    }
}
