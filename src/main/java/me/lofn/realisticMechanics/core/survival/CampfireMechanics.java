package me.lofn.realisticMechanics.core.survival;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class CampfireMechanics implements Listener {

    private static final double LIGHT_CHANCE = 0.35;

    @EventHandler
    public void onCampfirePlace(BlockPlaceEvent e){
        Block block = e.getBlockPlaced();

        if(!(block.getBlockData() instanceof Campfire campfire)) return;

        campfire.setLit(false);
        block.setBlockData(campfire);
    }

    @EventHandler
    public void onCampfireLight(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock() == null) return;

        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() != Material.FLINT_AND_STEEL) return;
        if (!(block.getBlockData() instanceof Campfire campfire)) return;
        if (campfire.isLit()) return;

        e.setCancelled(true);

        World world = block.getWorld();
        Location location = block.getLocation().add(0.5, 0.6, 0.5);

        world.spawnParticle(
                Particle.SMOKE,
                location,
                8,
                0.25,
                0.15,
                0.25,
                0.01
        );

        world.playSound(
                location,
                Sound.ITEM_FLINTANDSTEEL_USE,
                0.8f,
                1.2f
        );

        damageItem(player, item);

        if (Math.random() > LIGHT_CHANCE) return;

        campfire.setLit(true);
        block.setBlockData(campfire);

        world.spawnParticle(
                Particle.FLAME,
                location,
                12,
                0.2,
                0.1,
                0.2,
                0.01
        );
    }

    private void damageItem(Player player, ItemStack item) {
        if (!(item.getItemMeta() instanceof Damageable meta)) return;

        meta.setDamage(meta.getDamage() + 2);
        item.setItemMeta(meta);

        if (meta.getDamage() >= item.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        }
    }
}
