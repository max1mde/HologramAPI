package com.maximfiedler.hologramapi;

import com.maximfiedler.hologramapi.bstats.Metrics;
import com.maximfiedler.hologramapi.hologram.HologramManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class HologramAPI extends JavaPlugin {

    private static HologramManager hologramManager;

    @Override
    public void onEnable() {
        hologramManager = new HologramManager(this);
        new Metrics(this, 19375);
    }

    public static HologramManager getHologramManager() {
        return hologramManager;
    }

    public static HologramManager getHologramManager(Plugin plugin) {
        if(hologramManager == null) {
            hologramManager = new HologramManager(plugin);
        }
        return hologramManager;
    }

}
