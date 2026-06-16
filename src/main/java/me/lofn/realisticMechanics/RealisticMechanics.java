package me.lofn.realisticMechanics;

import me.lofn.realisticMechanics.core.calendar.CalendarCommand;
import me.lofn.realisticMechanics.core.calendar.FullMoonEffects;
import me.lofn.realisticMechanics.core.calendar.FullMoonListener;
import me.lofn.realisticMechanics.core.mobs.AggressiveWolves;
import me.lofn.realisticMechanics.core.physics.*;
import me.lofn.realisticMechanics.core.restrictions.ElytraRestrictions;
import me.lofn.realisticMechanics.core.restrictions.EssentialsRestrictions;
import me.lofn.realisticMechanics.core.restrictions.LeashRestrictions;
import me.lofn.realisticMechanics.core.restrictions.TeleportRestrictions;
import me.lofn.realisticMechanics.core.survival.CampfireMechanics;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RealisticMechanics extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        TreePhysics.init(this);
        Encumbrance.start(this);
        AggressiveWolves.start(this);
        FullMoonEffects.start(this);
        FragileIce.start(this);
        getLogger().info("Schedulers running");

        getServer().getPluginManager().registerEvents(new FallingTrees(), this);
        getServer().getPluginManager().registerEvents(new FullMoonListener(this), this);
        getServer().getPluginManager().registerEvents(new FullMoonEffects(), this);
        getServer().getPluginManager().registerEvents(new CampfireMechanics(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeashing(), this);
        PlayerLeashing.start(this);

        getServer().getPluginManager().registerEvents(new PlayerLeashing(), this);
        PlayerLeashing.start(this);

        getServer().getPluginManager().registerEvents(new LeashRestrictions(), this);
        getServer().getPluginManager().registerEvents(new TeleportRestrictions(), this);
        getServer().getPluginManager().registerEvents(new ElytraRestrictions(), this);

        PluginCommand calendarCommand = getCommand("calendar");

        if (calendarCommand != null) {
            calendarCommand.setExecutor(new CalendarCommand());
        } else {
            getLogger().warning("Command /calendar is missing from plugin.yml!");
        }

        boolean hasEssentials = getServer().getPluginManager().isPluginEnabled("Essentials");

        if(hasEssentials) {
            getServer().getPluginManager().registerEvents(new EssentialsRestrictions(), this);

            getLogger().info("EssentialsX detected, enabling Essentials integrations.");
        }

        getLogger().info("RealisticMechanics enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
