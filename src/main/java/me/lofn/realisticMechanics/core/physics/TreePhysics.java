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

    public static void fallStraightDown(Set<Block> logs) {
        if (logs.isEmpty()) return;

        World world = logs.iterator().next().getWorld();

        for (Block log : logs) {
            breakLeavesAround(log);

            world.spawnParticle(
                    Particle.BLOCK,
                    log.getLocation().add(0.5, 0.5, 0.5),
                    8,
                    log.getBlockData()
            );

            FallingBlock falling = world.spawn(
                    log.getLocation().add(0.5, 0.0, 0.5),
                    FallingBlock.class,
                    entity -> {
                        entity.setBlockData(log.getBlockData());
                        entity.setDropItem(true);
                        entity.setHurtEntities(true);
                    }
            );

            falling.setVelocity(new Vector(0, -0.25, 0));

            log.setType(Material.AIR);
        }
    }

    private static void breakLeavesAround(Block log) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block nearby = log.getRelative(x, y, z);

                    if (Tag.LEAVES.isTagged(nearby.getType())) {
                        nearby.breakNaturally();
                    }
                }
            }
        }
    }
}