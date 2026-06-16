package me.lofn.realisticMechanics.core.restrictions;

import me.lofn.realisticMechanics.core.physics.PlayerLeashing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class LeashRestrictions implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        if (PlayerLeashing.isLeashed(player)) {
            event.setCancelled(true);
        }
    }
}