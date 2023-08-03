package com.maximfiedler.hologramapi;

import com.maximfiedler.hologramapi.hologram.HologramManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class HologramAPI extends JavaPlugin {

    private static @Getter(AccessLevel.PUBLIC) HologramManager hologramManager;

    @Override
    public void onEnable() {
        hologramManager = new HologramManager(this);
    }

}
