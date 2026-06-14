package me.lofn.realisticMechanics.core.physics;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FallingTrees implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (!Tag.LOGS.isTagged(block.getType())) return;

        Vector hitDirection = block.getLocation()
                .toVector()
                .subtract(e.getPlayer().getLocation().toVector())
                .normalize();

        Set<Block> tree = getConnectedLogs(block);

        TreePhysics.fall(tree, hitDirection);
    }

    private Set<Block> getConnectedLogs(Block start) {
        Set<Block> logs = new HashSet<>();
        scan(start, logs, 0);
        return logs;
    }

    private void scan(Block block, Set<Block> logs, int depth) {
        if (depth > 80) return;
        if (logs.contains(block)) return;
        if (!Tag.LOGS.isTagged(block.getType())) return;

        logs.add(block);

        BlockFace[] faces = {
                BlockFace.UP,
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.EAST,
                BlockFace.WEST
        };

        for (BlockFace face : faces) {
            scan(block.getRelative(face), logs, depth + 1);
        }
    }
}