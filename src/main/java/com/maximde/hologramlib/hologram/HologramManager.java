package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.maximde.hologramlib.HologramLib;
import lombok.*;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class HologramManager {
    private final Map<TextHologram, BukkitRunnable> hologramAnimations = new ConcurrentHashMap<>();
    private final Map<String, TextHologram> hologramsMap = new ConcurrentHashMap<>();

    public List<TextHologram> getHolograms() {
        return new ArrayList<>(hologramsMap.values());
    }

    @Data
    @Builder
    @Accessors(fluent = true)
    public static class LeaderboardOptions {
        @Builder.Default
        private String title = "Leaderboard";

        @Builder.Default
        private String[] placeFormats = new String[] {
                "<color:#fdcc00><bold>1. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>",
                "<color:#dcdcdc><bold>2. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>",
                "<color:#e65f2f><bold>3. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>"
        };

        @Builder.Default
        private String defaultPlaceFormat = "<color:#ffb486><bold>{place}. </bold>{name}</color> <gray>{score}</gray> <white>{suffix}</white>";

        @Builder.Default
        private String titleFormat = "<gradient:#ff6000:#ffa42a>▛▀▀▀▀ {title} ▀▀▀▀▜</gradient>";

        @Builder.Default
        private String footerFormat = "<color:#ff6000>▙▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▟</color>";

        @Builder.Default
        private String suffix = "";

        @Builder.Default
        private float scale = 1.0f;

        @Builder.Default
        private boolean showEmptyPlaces = false;

        @Builder.Default
        private int maxDisplayEntries = 10;
    }

    public Optional<TextHologram> getHologram(String id) {
        return Optional.ofNullable(hologramsMap.get(id));
    }

    public TextHologram generateLeaderboard(Location location, Map<Integer, String> leaderboardData) {
        return generateLeaderboard(location, leaderboardData, LeaderboardOptions.builder().build());
    }

    public TextHologram generateLeaderboard(Location location, Map<Integer, String> leaderboardData, LeaderboardOptions options) {
        TextHologram hologram = new TextHologram(
                "leaderboard_" + UUID.randomUUID().toString().substring(0, 8)
        );

        spawn(hologram, location);
        updateLeaderboard(hologram, leaderboardData, options);
        return hologram;
    }

    public void updateLeaderboard(TextHologram hologram, Map<Integer, String> leaderboardData, LeaderboardOptions options) {
        Map<Integer, String> sortedData = new LinkedHashMap<>(leaderboardData);
        StringBuilder leaderboardText = new StringBuilder();

        leaderboardText.append(
                options.titleFormat()
                        .replace("{title}", options.title())
        ).append("\n\n");

        for (Map.Entry<Integer, String> entry : sortedData.entrySet()) {
            int place = entry.getKey();
            if (place > options.maxDisplayEntries()) break;
            if (!options.showEmptyPlaces() && entry.getValue().isEmpty()) continue;

            String[] parts = entry.getValue().split(":");
            String name = parts.length > 0 ? parts[0] : "Unknown";
            String score = parts.length > 1 ? parts[1] : "N/A";

            String placeFormat = place <= 3 && place <= options.placeFormats().length
                    ? options.placeFormats()[place - 1]
                    : options.defaultPlaceFormat();

            leaderboardText.append(
                    placeFormat
                            .replace("{place}", String.valueOf(place))
                            .replace("{name}", name)
                            .replace("{score}", score)
                            .replace("{suffix}", options.suffix())
            ).append("\n");
        }

        leaderboardText.append("\n").append(options.footerFormat());

        hologram.setMiniMessageText(leaderboardText.toString())
                .setScale(options.scale(), options.scale(), options.scale())
                .setBackgroundColor(520093695)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setBillboard(Display.Billboard.VERTICAL);

        hologram.update();
    }

    public TextHologram spawn(TextHologram textHologram, Location location) {
        textHologram.getInternalAccess().setLocation(location);
        textHologram.getInternalAccess().setEntityId(ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE));
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                textHologram.getEntityID(), Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
        Bukkit.getServer().getScheduler().runTask(HologramLib.getInstance(), () -> {
            textHologram.getInternalAccess().updateAffectedPlayers();
            textHologram.getInternalAccess().sendPacket(packet);
            textHologram.getInternalAccess().setDead(false);
        });
        textHologram.update();
        register(textHologram);
        return textHologram;
    }

    public void attach(TextHologram textHologram, int entityID) {
        this.attach(textHologram, entityID, true);
    }

    public void attach(TextHologram textHologram, int entityID, boolean persistent) {
        textHologram.attach(textHologram, entityID, persistent);
    }

    public boolean register(TextHologram textHologram) {
        if (textHologram == null) {
            return false;
        }
        if (hologramsMap.containsKey(textHologram.getId())) {
            Bukkit.getLogger().severe("Error: Hologram with ID " + textHologram.getId() + " is already registered.");
            return false;
        }
        hologramsMap.put(textHologram.getId(), textHologram);
        return true;
    }


    public boolean remove(TextHologram textHologram) {
        return textHologram != null && remove(textHologram.getId());
    }

    public boolean remove(String id) {
        TextHologram hologram = hologramsMap.remove(id);
        if (hologram != null) {
            hologram.kill();
            return true;
        }
        return false;
    }

    public void removeAll() {
        hologramsMap.values().forEach(TextHologram::kill);
        hologramsMap.clear();
    }

    public void applyAnimation(TextHologram hologram, TextAnimation textAnimation) {
        cancelAnimation(hologram);
        hologramAnimations.put(hologram, animateHologram(hologram, textAnimation));
    }

    public void cancelAnimation(TextHologram hologram) {
        Optional.ofNullable(hologramAnimations.remove(hologram)).ifPresent(BukkitRunnable::cancel);
    }

    private BukkitRunnable animateHologram(TextHologram hologram, TextAnimation textAnimation) {
        final BukkitRunnable animation = new BukkitRunnable() {
            int currentFrame = 0;
            public void run() {
                if (textAnimation.getTextFrames().isEmpty()) return;
                hologram.setMiniMessageText(textAnimation.getTextFrames().get(currentFrame));
                hologram.update();
                currentFrame = (currentFrame + 1) % textAnimation.getTextFrames().size();
            }
        };

        animation.runTaskTimerAsynchronously(HologramLib.getInstance(), textAnimation.getDelay(), textAnimation.getSpeed());
        return animation;
    }

    public void ifHologramExists(String id, Consumer<TextHologram> action) {
        Optional.ofNullable(hologramsMap.get(id)).ifPresent(action);
    }

    public boolean updateHologramIfExists(String id, Consumer<TextHologram> updateAction) {
        TextHologram hologram = hologramsMap.get(id);
        if (hologram != null) {
            updateAction.accept(hologram);
            return true;
        }
        return false;
    }

    /**
     * Creates a copy of an existing hologram at a new location
     * @param source The hologram to copy from
     * @param id The ID for the new hologram
     * @return The newly created hologram copy
     */
    public TextHologram copyHologram(TextHologram source, String id) {
        return this.spawn(source.copy(id), source.getLocation());
    }

    /**
     * Creates a copy of an existing hologram at a new location
     * @param source The hologram to copy from
     * @return The newly created hologram copy
     */
    public TextHologram copyHologram(TextHologram source) {
        return this.spawn(source.copy(), source.getLocation());
    }
}