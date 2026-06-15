package me.lofn.realisticMechanics;

import me.lofn.realisticMechanics.core.mobs.AggressiveWolves;
import me.lofn.realisticMechanics.core.physics.Encumbrance;
import me.lofn.realisticMechanics.core.physics.FallingTrees;
import me.lofn.realisticMechanics.core.physics.TreePhysics;
import org.bukkit.plugin.java.JavaPlugin;

public final class RealisticMechanics extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        TreePhysics.init(this);
        Encumbrance.start(this);
        AggressiveWolves.start(this);

        getServer().getPluginManager().registerEvents(
                new FallingTrees(),
                this
        );

        getLogger().info("RealisticMechanics enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
