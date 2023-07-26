package com.maximfiedler.hologramapi;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class HologramAPI extends JavaPlugin {

    private static HologramAPI hologramAPI;

    @Override
    public void onEnable() {
        hologramAPI = this;
        getServer().getLogger().log(Level.INFO, "Enabled HologramAPI by Maxim Fiedler");
    }

    public static HologramAPI getHologramAPI() {
        return hologramAPI;
    }

    public List<TextHologram> getHologramsByID(String id) {
        List<TextDisplay> displays = new ArrayList<>();
        List<TextHologram> holograms = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity instanceof TextDisplay textDisplay)) continue;
                if(textDisplay.getScoreboardTags().contains(id+"_hologram_api")) continue;
                displays.add(textDisplay);
            }
        }
        for(TextDisplay textDisplay : displays) {
            TextHologram hologram = new TextHologram("id");
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
