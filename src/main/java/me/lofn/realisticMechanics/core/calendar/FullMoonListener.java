package me.lofn.realisticMechanics.core.calendar;

import me.lofn.realisticMechanics.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FullMoonListener implements Listener {

    private final JavaPlugin plugin;

    public FullMoonListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var world = player.getWorld();

        if(!TimeUtils.isFullMoon(world)) return;

        if(TimeUtils.isNight(world)){
            player.sendMessage("§cThe full moon hangs above. You feel weaker tonight...");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.playSound(
                        player.getLocation(),
                        Sound.ENTITY_WOLF_WHINE,
                        1.0f,
                        1.5f
                );
            }, 40L);

        } else {
            player.sendMessage("§cThe coming night will be a full moon...");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.playSound(
                        player.getLocation(),
                        Sound.ENTITY_WOLF_WHINE,
                        0.7f,
                        1.5f
                );
            }, 40L);
        }
    }
}
