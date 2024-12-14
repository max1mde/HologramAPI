package com.maximde.hologramapi.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.maximde.hologramapi.HologramAPI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class HologramManager {

    private final Plugin plugin;

    @Getter
    private final Map<TextHologram, BukkitRunnable> hologramAnimations = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, TextHologram> hologramsMap = new ConcurrentHashMap<>();

    public List<TextHologram> getHolograms() {
        return new ArrayList<>(this.hologramsMap.values());
    }

    @Data
    @Builder
    public static class LeaderboardOptions {
        @Builder.Default
        private String title = "Leaderboard";
        @Builder.Default
        private String titleFormat = "<gradient:gold:yellow>▛▀▀ {title} ▀▀▜</gradient>";
        @Builder.Default
        private String[] placeFormats = new String[] {
                "<gold><bold>{place}. </bold>{name} <gray>{score}</gray>",
                "<gray><bold>{place}. </bold>{name} <dark_gray>{score}</dark_gray>",
                "<red><bold>{place}. </bold>{name} <dark_red>{score}</dark_red>"
        };
        @Builder.Default
        private String defaultPlaceFormat = "<yellow><bold>{place}. </bold>{name} <gray>{score}</gray>";
        @Builder.Default
        private String footerFormat = "<gradient:gold:yellow>▙▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▟</gradient>";
        @Builder.Default
        private String suffix = "";
        @Builder.Default
        private float scale = 1.0f;
        @Builder.Default
        private boolean showEmptyPlaces = false;
        @Builder.Default
        private int maxDisplayEntries = 10;
    }

    public TextHologram generateLeaderboard(Location location, Map<Integer, String> leaderboardData) {
        return generateLeaderboard(location, leaderboardData, LeaderboardOptions.builder().build());
    }

    public TextHologram generateLeaderboard(Location location, Map<Integer, String> leaderboardData, LeaderboardOptions options) {
        TextHologram hologram = new TextHologram(
                "leaderboard_" + UUID.randomUUID().toString().substring(0, 8)
        );

        updateLeaderboard(hologram, leaderboardData, options);
        this.spawn(hologram, location);
        return hologram;
    }

    public void updateLeaderboard(TextHologram hologram, Map<Integer, String> leaderboardData, LeaderboardOptions options) {
        Map<Integer, String> sortedData = new LinkedHashMap<>(leaderboardData);
        StringBuilder leaderboardText = new StringBuilder();

        leaderboardText.append(
                options.getTitleFormat()
                        .replace("{title}", options.getTitle())
        ).append("\n\n");

        for (Map.Entry<Integer, String> entry : sortedData.entrySet()) {
            int place = entry.getKey();
            if (place > options.getMaxDisplayEntries()) break;
            if (!options.isShowEmptyPlaces() && entry.getValue().isEmpty()) continue;

            String[] parts = entry.getValue().split(":");
            String name = parts.length > 0 ? parts[0] : "Unknown";
            String score = parts.length > 1 ? parts[1] : "N/A";

            String placeFormat = place <= 3 && place <= options.getPlaceFormats().length
                    ? options.getPlaceFormats()[place - 1]
                    : options.getDefaultPlaceFormat();

            leaderboardText.append(
                    placeFormat
                            .replace("{place}", String.valueOf(place))
                            .replace("{name}", name)
                            .replace("{score}", score)
                            .replace("{suffix}", options.getSuffix())
            ).append("\n");
        }

        leaderboardText.append("\n").append(options.getFooterFormat());

        hologram.setMiniMessageText(leaderboardText.toString())
                .setScale(options.getScale(), options.getScale(), options.getScale())
                .setBillboard(Display.Billboard.VERTICAL)
                .setAlignment(TextDisplay.TextAlignment.CENTER);
    }

    public void spawn(TextHologram textHologram, Location location) {
        textHologram.getInternalAccess().setLocation(location);
        textHologram.getInternalAccess().setEntityId(ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE));
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                textHologram.getEntityID(), Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
        );
        Bukkit.getServer().getScheduler().runTask(HologramAPI.getInstance().get(), () -> {
            textHologram.getInternalAccess().updateAffectedPlayers();
            textHologram.getInternalAccess().sendPacket(packet);
            textHologram.getInternalAccess().setDead(false);
        });
        textHologram.update();
        register(textHologram);
    }


    public void attach(TextHologram textHologram, int entityID) {
        textHologram.attach(textHologram, entityID);
    }

    public void register(TextHologram textHologram) {
        this.hologramsMap.put(textHologram.getId(), textHologram);
    }

    public void remove(TextHologram textHologram) {
        remove(textHologram.getId());
    }

    public void remove(String id) {
        Optional.ofNullable(this.hologramsMap.remove(id)).ifPresent(TextHologram::kill);
    }

    public void removeAll() {
        this.hologramsMap.values().forEach(TextHologram::kill);
        this.hologramsMap.clear();
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

        animation.runTaskTimerAsynchronously(this.plugin, textAnimation.getDelay(), textAnimation.getSpeed());
        return animation;
    }
}
