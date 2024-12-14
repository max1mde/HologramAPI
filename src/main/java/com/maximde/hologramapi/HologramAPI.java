package com.maximde.hologramapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.maximde.hologramapi.bstats.Metrics;
import com.maximde.hologramapi.hologram.HologramManager;
import com.maximde.hologramapi.utils.ItemsAdderHolder;
import com.maximde.hologramapi.utils.ReplaceText;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.logging.Level;

public final class HologramAPI extends JavaPlugin {

    @Getter
    private static HologramAPI instance;

    @Getter
    private ReplaceText replaceText;

    @Getter
    private PlayerManager playerManager;

    private HologramManager hologramManager;

    public static Optional<HologramManager> getManager() {
        if (instance == null || instance.hologramManager == null) {
            Bukkit.getLogger().log(Level.SEVERE, "HologramAPI has not been initialized yet. Make sure to include 'HologramAPI' as a dependency in your plugin.yml and that the plugin is enabled!");
            return Optional.empty();
        }
        return Optional.of(instance.hologramManager);
    }

    public static Optional<HologramManager> getManager(Plugin plugin) {
        if (!(plugin instanceof JavaPlugin)) {
            Bukkit.getLogger().log(Level.SEVERE, "Unable to initialize HologramAPI: Provided plugin is not a valid JavaPlugin.");
            return Optional.empty();
        }

        if (instance != null && instance.hologramManager != null) {
            return Optional.of(instance.hologramManager);
        }

        Bukkit.getLogger().log(Level.INFO, "Initializing HologramAPI from a shaded plugin context.");
        HologramAPI api = new HologramAPI();
        api.onLoad();
        api.onEnable();
        return Optional.ofNullable(api.hologramManager);
    }

    @Override
    public void onLoad() {
        instance = this;
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig config = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();
        EntityLib.init(platform, config);

        this.playerManager = PacketEvents.getAPI().getPlayerManager();
        this.hologramManager = new HologramManager();

        new Metrics(this, 19375);

        try {
            this.replaceText = new ItemsAdderHolder();
        } catch (ClassNotFoundException e) {
            this.replaceText = text -> text;
        }
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
