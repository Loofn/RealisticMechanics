package me.lofn.realisticMechanics.core.physics;

import net.ess3.api.events.TPARequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLeashing implements Listener {

    private static final double MAX_DISTANCE = 12.0;
    private static final double PULL_START_DISTANCE = 3.0;
    private static final double PULL_STRENGTH = 0.18;

    private static final Map<UUID, UUID> LEASHED_PLAYERS = new HashMap<>();

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, PlayerLeashing::tick, 20L, 10L);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!(e.getRightClicked() instanceof Player target)) return;

        Player leasher = e.getPlayer();

        if (leasher.getInventory().getItemInMainHand().getType() != Material.LEAD) return;
        if (canBeLeashed(target)) return;

        e.setCancelled(true);

        UUID targetId = target.getUniqueId();

        if (LEASHED_PLAYERS.containsKey(targetId)) {
            LEASHED_PLAYERS.remove(targetId);
            target.sendMessage("§cYou are no longer leashed.");
            leasher.sendMessage("§cYou have released " + target.getName() + ".");
            return;
        }

        LEASHED_PLAYERS.put(targetId, leasher.getUniqueId());

        target.sendMessage("§cYou have been leashed by " + leasher.getName() + ".");
        leasher.sendMessage("§aYou leashed " + target.getName() + ".");

    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        if (LEASHED_PLAYERS.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private static void tick() {
        LEASHED_PLAYERS.entrySet().removeIf(entry -> {
            Player target = Bukkit.getPlayer(entry.getKey());
            Player leasher = Bukkit.getPlayer(entry.getValue());

            if (target == null || leasher == null) return true;
            if (canBeLeashed(target)) return true;
            if (!target.getWorld().equals(leasher.getWorld())) return true;

            double distance = target.getLocation().distance(leasher.getLocation());

            if (distance > MAX_DISTANCE) {
                target.sendMessage("§7The lead snaps.");
                leasher.sendMessage("§7The lead snaps.");
                return true;
            }

            if (distance < PULL_START_DISTANCE) return false;

            Vector direction = leasher.getLocation()
                    .toVector()
                    .subtract(target.getLocation().toVector())
                    .normalize();

            Vector velocity = target.getVelocity();

            velocity.add(direction.multiply(PULL_STRENGTH));
            velocity.setY(Math.max(velocity.getY(), 0.08));

            target.setVelocity(velocity);

            return false;
        });
    }

    private static boolean canBeLeashed(Player player) {
        if (LEASHED_PLAYERS.containsKey(player.getUniqueId())) {
            return true;
        }
        return player.getGameMode() == GameMode.CREATIVE;
    }

    public static boolean isLeashed(Player player) {
        return LEASHED_PLAYERS.containsKey(player.getUniqueId());
    }

}
