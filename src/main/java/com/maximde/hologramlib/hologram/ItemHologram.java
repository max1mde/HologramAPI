package com.maximde.hologramlib.hologram;


import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maximde.hologramlib.utils.Vector3F;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.joml.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;


public class ItemHologram extends Hologram<ItemHologram> {

    @Getter
    @Accessors(chain = true)
    protected ItemDisplayMeta.DisplayType displayType = ItemDisplayMeta.DisplayType.FIXED;

    @Getter
    @Accessors(chain = true)
    protected boolean onFire = false;

    @Getter
    @Accessors(chain = true)
    protected ItemStack item = new ItemStack.Builder().type(ItemTypes.DIAMOND_BLOCK).build();

    @Getter
    @Accessors(chain = true)
    protected boolean glowing = false;

    @Getter
    @Accessors(chain = true)
    protected int glowColor = Color.YELLOW.getRGB();

    public ItemHologram(String id, RenderMode renderMode) {
        super(id, renderMode, EntityTypes.ITEM_DISPLAY);
    }

    public ItemHologram(String id) {
        this(id, RenderMode.NEARBY);
    }

    @Override
    protected WrapperPlayServerEntityMetadata createMeta() {
        ItemDisplayMeta meta = (ItemDisplayMeta) EntityMeta.createMeta(super.entityID, EntityTypes.ITEM_DISPLAY);
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(super.toVector3f(this.translation));
        meta.setScale(super.toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setViewRange((float) this.viewRange);
        meta.setDisplayType(this.displayType);
        meta.setOnFire(this.onFire);
        meta.setItem(this.item);
        meta.setGlowing(this.glowing);
        meta.setGlowColorOverride(this.glowColor);
        return meta.createPacket();
    }


    @Override
    protected ItemHologram copy() {
        //TODO
        return null;
    }

    @Override
    protected ItemHologram copy(String id) {
        //TODO
        return null;
    }

    public PlayerProfile getPlayerProfile(UUID uuid) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL(this.getPlayerSkinUrl(uuid)));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
        }
        profile.setTextures(textures);
        return profile;
    }

    public String getPlayerSkinUrl(UUID uuid) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                uuid.toString().replace("-", "")).openConnection().getInputStream())) {

            JsonObject profile = JsonParser.parseReader(reader).getAsJsonObject();
            if (!profile.has("properties")) return null;

            String encodedTextures = profile.getAsJsonArray("properties")
                    .get(0).getAsJsonObject()
                    .get("value").getAsString();

            JsonObject textures = JsonParser.parseString(new String(Base64.getDecoder().decode(encodedTextures)))
                    .getAsJsonObject()
                    .getAsJsonObject("textures");

            return textures.has("SKIN") ? textures.getAsJsonObject("SKIN").get("url").getAsString() : null;
        }
    }
}