package com.maximde.hologramapi.hologram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class HologramManager {

    private final Plugin plugin;

    @Getter
    private final HashMap<TextHologram, BukkitRunnable> hologramAnimations = new HashMap<>();

    @Getter
    private final HashMap<String, TextHologram> hologramsMap = new HashMap<>();

    public List<TextHologram> getHolograms() {
        return (List<TextHologram>) this.hologramsMap.values();
    }

    public void spawn(TextHologram textHologram) {

    }

    public void remove(TextHologram textHologram) {
        remove(textHologram.getId());
    }

    public void remove(String id) {
        if(!hologramsMap.containsKey(id)) return;
        this.hologramsMap.get(id).kill();
        this.hologramsMap.remove(id);
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
        if(hologramAnimations.containsKey(hologram)) {
            hologramAnimations.get(hologram).cancel();
            hologramAnimations.remove(hologram);
        }
    }

    private BukkitRunnable animateHologram(TextHologram hologram, TextAnimation textAnimation) {
        final BukkitRunnable animation = new BukkitRunnable() {
            int currentFrame = 0;
            public void run() {
                if(textAnimation.getTextFrames().isEmpty()) return;
                hologram.setText(textAnimation.getTextFrames().get(currentFrame));
                hologram.update();
                currentFrame++;
                if(currentFrame >= textAnimation.getTextFrames().size()) currentFrame = 0;
            }
        };

        animation.runTaskTimerAsynchronously(this.plugin, textAnimation.getDelay(), textAnimation.getSpeed());
        return animation;
    }
}
