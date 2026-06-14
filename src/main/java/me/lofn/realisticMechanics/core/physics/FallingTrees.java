package me.lofn.realisticMechanics.core.physics;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FallingTrees implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Block b = e.getBlock();

        if (!Tag.LOGS.isTagged(b.getType())) return;

        Vector hitDirection = e.getPlayer().getLocation()
                .toVector()
                .subtract(b.getLocation().toVector())
                .normalize();

        Set<Block> tree = getConnectedLogs(b);

        TreePhysics.fall(tree, hitDirection);
    }

    public static class TreePhysics {

        private static JavaPlugin plugin;

        public static void init(JavaPlugin pl) {
            plugin = pl;
        }

        public static void fall(Set<Block> logs, Vector hitDir) {

            Vector fallDirection = calculateFallDirection(logs, hitDir);
            World world = logs.iterator().next().getWorld();

            // 🌫️ WARNINGS (run ONCE)
            for (Block b : logs) {
                world.spawnParticle(Particle.CLOUD,
                        b.getLocation().add(0.5, 0.5, 0.5),
                        2);

                world.playSound(
                        b.getLocation(),
                        Sound.BLOCK_WOOD_BREAK,
                        1f,
                        0.6f
                );
            }

            // ⏳ SINGLE COLLAPSE TASK (FIXED)
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                for (Block b : logs) {
                    spawnFallingLog(b, fallDirection);
                    b.setType(Material.AIR);
                }

            }, 10L);
        }
    }

    private static Vector calculateFallDirection(Set<Block> logs, Vector hitDir) {

        Vector center = new Vector(0, 0, 0);

        for (Block b : logs) {
            center.add(b.getLocation().toVector());
        }

        center.multiply(1.0 / logs.size());

        // bias direction slightly downward + hit influence
        Vector direction = hitDir.clone();

        direction.setY(-0.3); // gravity bias

        // small randomness like Valheim
        direction.add(new Vector(
                (Math.random() - 0.5) * 0.2,
                0,
                (Math.random() - 0.5) * 0.2
        ));

        return direction.normalize();
    }

    private Set<Block> getConnectedLogs(Block start) {
        Set<Block> logs = new HashSet<>();
        scan(start, logs, 0);
        return logs;
    }

    private void scan(Block b, Set<Block> logs, int depth) {

        if (depth > 80) return;
        if (logs.contains(b)) return;
        if (!Tag.LOGS.isTagged(b.getType())) return;

        logs.add(b);

        BlockFace[] faces = {
                BlockFace.UP,
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.EAST,
                BlockFace.WEST
        };

        for (BlockFace face : faces) {
            scan(b.getRelative(face), logs, depth + 1);
        }
    }

    private static void spawnFallingLog(Block b, Vector dir) {

        FallingBlock entity = b.getWorld().spawn(
                b.getLocation().add(0.5, 0.5, 0.5),
                FallingBlock.class,
                fb -> {
                    fb.setBlockData(b.getBlockData());
                    fb.setDropItem(true);
                    fb.setHurtEntities(true);
                }
        );

        entity.setVelocity(dir.multiply(0.6).setY(0.4));
    }
}
