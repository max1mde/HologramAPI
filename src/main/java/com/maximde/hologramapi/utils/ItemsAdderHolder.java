package com.maximde.hologramapi.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class ItemsAdderHolder implements ReplaceText {

    public ItemsAdderHolder() throws ClassNotFoundException {
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) {
            Bukkit.getLogger().log(Level.WARNING, "[HologramAPI] ItemsAdder plugin not found! No custom emojis support.");
            throw new ClassNotFoundException();
        }
    }

    @Override
    public String replace(String s) {
        return FontImageWrapper.replaceFontImages(s);
    }
}
