package me.lofn.realisticMechanics.core.calendar;

import me.lofn.realisticMechanics.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FullMoonEffects implements Listener {

    public static void start(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!TimeUtils.isFullMoon(player.getWorld())) continue;
                if(!TimeUtils.isNight(player.getWorld())) continue;

                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.WEAKNESS,
                        240,
                        0,
                        true,
                        false,
                        true
                ));

            }
        }, 20L, 100L);
    }

    @EventHandler
    public void onZombieSpawn(CreatureSpawnEvent e) {
        if (e.getEntityType() != EntityType.ZOMBIE) return;
        if(!(e.getEntity() instanceof Zombie zombie)) return;

        if (!TimeUtils.isFullMoon(zombie.getWorld())) return;
        if (!TimeUtils.isNight(zombie.getWorld())) return;

        var health = zombie.getAttribute(Attribute.MAX_HEALTH);
        if (health != null){
            health.setBaseValue(30.0);
            zombie.setHealth(30.0);
        }

        var damage = zombie.getAttribute(Attribute.ATTACK_DAMAGE);
        if (damage != null){
            damage.setBaseValue(damage.getBaseValue() + 2.0);
        }
    }
}
