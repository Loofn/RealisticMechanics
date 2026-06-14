package me.lofn.realisticMechanics;

import me.lofn.realisticMechanics.core.physics.FallingTrees;import org.bukkit.plugin.java.JavaPlugin;

public final class RealisticMechanics extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        FallingTrees.TreePhysics.init(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
