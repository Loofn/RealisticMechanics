package me.lofn.realisticMechanics.core.physics;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FallingTrees implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block broken = event.getBlock();

        if (!Tag.LOGS.isTagged(broken.getType())) return;

        Block above = broken.getRelative(BlockFace.UP);

        if (!Tag.LOGS.isTagged(above.getType())) return;

        Set<Block> treeLogs = getConnectedTreeLogsAbove(above);

        if (!looksLikeTree(treeLogs)) return;

        TreePhysics.fallStraightDown(treeLogs);
    }

    private Set<Block> getConnectedTreeLogsAbove(Block start) {
        Set<Block> logs = new HashSet<>();
        scanAbove(start, logs, 0);
        return logs;
    }

    private void scanAbove(Block block, Set<Block> logs, int depth) {
        if (depth > 96) return;
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
            scanAbove(block.getRelative(face), logs, depth + 1);
        }
    }

    private boolean looksLikeTree(Set<Block> logs) {
        if (logs.size() < 2) return false;

        int nearbyLeaves = 0;

        for (Block log : logs) {
            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        Block nearby = log.getRelative(x, y, z);

                        if (isNaturalLeaf(nearby)) {
                            nearbyLeaves++;
                        }
                    }
                }
            }
        }

        return nearbyLeaves >= 4;
    }

    private boolean isNaturalLeaf(Block block) {

        if (!Tag.LEAVES.isTagged(block.getType())) {
            return false;
        }

        if (!(block.getBlockData() instanceof Leaves leaves)) {
            return false;
        }

        // false = naturally generated or grown from sapling
        return !leaves.isPersistent();
    }
}