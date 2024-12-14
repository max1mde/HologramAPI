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
    private static HologramManager manager;

    @Getter
    private static ReplaceText replaceText;

    @Getter
    private static PlayerManager playerManager;

    private static HologramAPI instance;

    public static Optional<HologramAPI> getInstance() {
        if (instance == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Tried to access the HologramAPI but it was not initialized yet! Add depends 'HologramAPI' to your plugin.yml and make sure the plugin itself is on the server! Otherwise use HologramAPI#getInstance(Plugin <your plugin instance>) if you are shading the API!");
        }
        return Optional.ofNullable(instance);
    }

    public static Optional<HologramAPI> getInstance(Plugin plugin) {
        if (plugin == null) return Optional.empty();

        if(instance != null) return Optional.of(instance);

        if (plugin instanceof JavaPlugin javaPlugin) {
            javaPlugin.getLogger().log(Level.INFO, "Initializing HologramAPI from shaded plugin context.");
            instance = new HologramAPI();
            instance.onLoad();
            instance.onEnable();
            return Optional.of(instance);
        }

        Bukkit.getLogger().log(Level.SEVERE, "Unable to initialize HologramAPI: Provided plugin is not a valid JavaPlugin.");
        return Optional.empty();
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        if(instance == null) instance = this;
        PacketEvents.getAPI().init();

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();

        EntityLib.init(platform, settings);

        playerManager = PacketEvents.getAPI().getPlayerManager();

        manager = new HologramManager(this);
        new Metrics(this, 19375);

        try {
            replaceText = new ItemsAdderHolder();
        } catch (ClassNotFoundException exception) {
            replaceText = s -> s;
        }
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
