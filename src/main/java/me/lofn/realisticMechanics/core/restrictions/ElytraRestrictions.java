package me.lofn.realisticMechanics.core.restrictions;

import me.lofn.realisticMechanics.core.physics.PlayerLeashing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class ElytraRestrictions implements Listener {

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!event.isGliding()) return;

        if (PlayerLeashing.isLeashed(player)) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot use elytra while leashed.");
        }
    }
}
