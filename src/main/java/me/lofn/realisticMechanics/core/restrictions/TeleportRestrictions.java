package me.lofn.realisticMechanics.core.restrictions;

import me.lofn.realisticMechanics.core.physics.PlayerLeashing;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportRestrictions implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (!PlayerLeashing.isLeashed(player)) return;

        event.setCancelled(true);
        player.sendMessage("§cYou cannot teleport while leashed.");
    }
}