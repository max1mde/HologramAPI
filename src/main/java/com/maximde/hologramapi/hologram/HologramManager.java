package com.maximde.hologramapi.hologram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public void spawn(TextHologram textHologram, Location location) {
        textHologram.spawn(location);
        this.hologramsMap.put(textHologram.getId(), textHologram);
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
