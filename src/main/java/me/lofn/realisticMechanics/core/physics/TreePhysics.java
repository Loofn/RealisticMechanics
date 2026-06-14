package me.lofn.realisticMechanics.core.physics;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Set;

public class TreePhysics {

    private static JavaPlugin plugin;

    public static void init(JavaPlugin pl) {
        plugin = pl;
    }

    public static void fall(Set<Block> logs, Vector hitDir) {
        if (logs.isEmpty()) return;

        Vector fallDirection = calculateFallDirection(logs, hitDir);
        World world = logs.iterator().next().getWorld();

        for (Block block : logs) {
            world.spawnParticle(
                    Particle.CLOUD,
                    block.getLocation().add(0.5, 0.5, 0.5),
                    2
            );

            world.playSound(
                    block.getLocation(),
                    Sound.BLOCK_WOOD_BREAK,
                    1f,
                    0.6f
            );
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Block block : logs) {
                spawnFallingLog(block, fallDirection);
                block.setType(Material.AIR);
            }
        }, 10L);
    }

    private static Vector calculateFallDirection(Set<Block> logs, Vector hitDir) {
        Vector direction = hitDir.clone();

        direction.setY(-0.3);

        direction.add(new Vector(
                (Math.random() - 0.5) * 0.2,
                0,
                (Math.random() - 0.5) * 0.2
        ));

        return direction.normalize();
    }

    private static void spawnFallingLog(Block block, Vector direction) {
        FallingBlock fallingBlock = block.getWorld().spawn(
                block.getLocation().add(0.5, 0.5, 0.5),
                FallingBlock.class,
                entity -> {
                    entity.setBlockData(block.getBlockData());
                    entity.setDropItem(true);
                    entity.setHurtEntities(true);
                }
        );

        fallingBlock.setVelocity(direction.clone().multiply(0.6).setY(0.4));
    }
}