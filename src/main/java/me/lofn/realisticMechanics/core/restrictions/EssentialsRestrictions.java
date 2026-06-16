package me.lofn.realisticMechanics.core.restrictions;

import me.lofn.realisticMechanics.core.physics.PlayerLeashing;
import net.ess3.api.events.TPARequestEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsRestrictions implements Listener {

    @EventHandler
    public void onEssentialsTpaRequest(TPARequestEvent event) {
        Player requester = event.getRequester().getPlayer();
        Player target = event.getTarget().getBase();

        if (requester == null || target == null) return;

        if (PlayerLeashing.isLeashed(requester)) {
            event.setCancelled(true);
            requester.sendMessage("§cYou cannot send teleport requests while leashed.");
            return;
        }

        if (PlayerLeashing.isLeashed(target)) {
            event.setCancelled(true);
            requester.sendMessage("§cThat player is restrained and cannot receive teleport requests.");
            target.sendMessage("§7" + requester.getName() + " tried to send you a teleport request.");
        }
    }
}
