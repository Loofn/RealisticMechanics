package me.lofn.realisticMechanics.core.overrides;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CampfireBreaking implements Listener {

    @EventHandler
    public void onCampfireBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (!(block.getBlockData() instanceof Campfire)) {
            return;
        }

        e.setDropItems(false);

        World world = block.getWorld();
        Location location = block.getLocation().add(0.5, 0.5, 0.5);

        world.dropItemNaturally(location, new ItemStack(Material.COAL, 2));

    }
}
