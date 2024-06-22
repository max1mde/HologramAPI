package com.maximde.hologramapi;

import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.maximde.hologramapi.bstats.Metrics;
import com.maximde.hologramapi.hologram.HologramManager;
import com.maximde.hologramapi.utils.ItemsAdderHolder;
import com.maximde.hologramapi.utils.ReplaceText;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class HologramAPI extends JavaPlugin {

    @Getter
    private static HologramManager hologram;

    @Getter
    private static ReplaceText replaceText;

    @Getter
    private static PlayerManager playerManager;

    @Override
    public void onEnable() {
        hologram = new HologramManager(this);
        new Metrics(this, 19375);

        try {
            replaceText = new ItemsAdderHolder();
        } catch (ClassNotFoundException exception) {
            replaceText = s -> s;
        }
    }

}
