package com.maximde.hologramapi;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.maximde.hologramapi.bstats.Metrics;
import com.maximde.hologramapi.hologram.HologramManager;
import com.maximde.hologramapi.utils.ItemsAdderHolder;
import com.maximde.hologramapi.utils.ReplaceText;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.logging.Level;

public final class HologramAPI extends JavaPlugin {

    private static volatile HologramAPI instance;

    @Getter
    private ReplaceText replaceText;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private HologramManager hologramManager;

    public static Optional<HologramManager> getManager() {
        return Optional.ofNullable(getInstance().hologramManager)
                .or(() -> {
                    Bukkit.getLogger().log(Level.SEVERE,
                            "HologramAPI has not been initialized yet. " +
                                    "Ensure 'HologramAPI' is included as a dependency in your plugin.yml.");
                    return Optional.empty();
                });
    }

    @SneakyThrows
    public static Optional<HologramManager> getManager(Plugin plugin) {
        if (!(plugin instanceof JavaPlugin)) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Unable to initialize HologramAPI: Provided plugin is not a valid JavaPlugin.");
            return Optional.empty();
        }

        if (instance != null && instance.hologramManager != null) {
            return Optional.of(instance.hologramManager);
        }

        synchronized (HologramAPI.class) {
            if (instance == null) {
                Bukkit.getLogger().log(Level.INFO,
                        "Initializing HologramAPI from a shaded plugin context.");

                HologramAPI api = new HologramAPI();
                api.onLoad();
                api.onEnable();

                return Optional.ofNullable(api.hologramManager);
            }
        }

        return Optional.ofNullable(instance.hologramManager);
    }

    @Override
    public void onLoad() {
        instance = this;
        Optional.ofNullable(SpigotPacketEventsBuilder.build(this))
                .ifPresentOrElse(
                        PacketEvents::setAPI,
                        () -> getLogger().severe("Failed to build PacketEvents API")
                );

        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        try {
            initializePacketEvents();
            initializeEntityLib();
            initializeManagers();
            initializeMetrics();
            initializeReplaceText();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable HologramAPI", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void initializePacketEvents() {
        PacketEvents.getAPI().init();
    }

    private void initializeEntityLib() {
        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig config = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();
        EntityLib.init(platform, config);
    }

    private void initializeManagers() {
        this.playerManager = PacketEvents.getAPI().getPlayerManager();
        this.hologramManager = new HologramManager();
    }

    private void initializeMetrics() {
        new Metrics(this, 19375);
    }

    private void initializeReplaceText() {
        this.replaceText = createReplaceTextInstance()
                .orElse(text -> text);
    }

    private Optional<ReplaceText> createReplaceTextInstance() {
        try {
            return Optional.of(new ItemsAdderHolder());
        } catch (ClassNotFoundException e) {
            getLogger().warning("ItemsAdder not found. Using default text replacement.");
            return Optional.empty();
        }
    }

    @Override
    public void onDisable() {
        Optional.ofNullable(PacketEvents.getAPI()).ifPresent(PacketEventsAPI::terminate);
        instance = null;
    }

    public static synchronized HologramAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("HologramAPI has not been initialized");
        }
        return instance;
    }
}