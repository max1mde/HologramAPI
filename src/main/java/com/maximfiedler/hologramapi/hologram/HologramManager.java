package com.maximfiedler.hologramapi.hologram;

import com.maximfiedler.hologramapi.HologramAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HologramManager {

    private final Plugin plugin;

    private HashMap<TextHologram, BukkitRunnable> hologramAnimations = new HashMap<>();

    public HologramManager(Plugin plugin) {
        this.plugin = plugin;
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

    /**
     * Removes all text-displays if it has a scoreboard tag with the given ID
     * @param id Hologram ID / scoreboard tag
     */
    public void removeAll(String id) {
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity instanceof TextDisplay textDisplay)) continue;
                if(!textDisplay.getScoreboardTags().contains(id + "_hologram_api")) continue;
                textDisplay.remove();
            }
        }
    }

    /**
     * Get all holograms with a specific id in the form of TextHologram objects in a list
     * @param id the hologram id which is saved as a scoreboard tag
     * @return TextHologram list
     */
    public List<TextHologram> getHologramsByID(String id) {
        List<TextDisplay> displays = new ArrayList<>();
        List<TextHologram> holograms = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity instanceof TextDisplay textDisplay)) continue;
                if(!textDisplay.getScoreboardTags().contains(id + "_hologram_api")) continue;
                displays.add(textDisplay);
            }
        }
        for(TextDisplay textDisplay : displays) {
            TextHologram hologram = new TextHologram("id");
            hologram.setDisplay(textDisplay);
            hologram.setText(textDisplay.getText());
            hologram.setBillboard(textDisplay.getBillboard());
            hologram.setBackgroundColor(textDisplay.getBackgroundColor());
            hologram.setAlignment(textDisplay.getAlignment());
            hologram.setViewRange(textDisplay.getViewRange());
            hologram.setSeeThrough(textDisplay.isSeeThrough());
            hologram.setTextShadow(textDisplay.isShadowed());
            hologram.setLineWidth(textDisplay.getLineWidth());
            hologram.setTextOpacity(textDisplay.getTextOpacity());
            hologram.setBrightness(textDisplay.getBrightness());
            var transformation = textDisplay.getTransformation();
            hologram.setSize(transformation.getScale());
            hologram.setLeftRotation(transformation.getLeftRotation());
            hologram.setRightRotation(transformation.getRightRotation());
            hologram.setTranslation(transformation.getTranslation());
            holograms.add(hologram);
        }
        displays.clear();
        return holograms;
    }

}
