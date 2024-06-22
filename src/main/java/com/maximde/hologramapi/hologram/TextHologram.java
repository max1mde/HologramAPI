package com.maximde.hologramapi.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.maximde.hologramapi.HologramAPI;
import com.maximde.hologramapi.utils.MiniMessage;
import com.maximde.hologramapi.utils.Vector3F;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TextHologram {

    @Getter
    private final String id;

    @Getter @Accessors(chain = true)
    protected int entityID;

    // No setters because own implementation of setters at the bottom
    protected Component text = Component.text("Hologram API");
    protected Vector3f scale = new Vector3f(0,0,0);
    protected Vector3f translation = new Vector3f(0,  0.7F, 0);

    @Setter @Getter @Accessors(chain = true)
    protected Display.Billboard billboard = Display.Billboard.CENTER;
    @Setter @Getter @Accessors(chain = true)
    protected int interpolationDurationTicks = 20;
    @Setter @Getter @Accessors(chain = true)
    protected double viewRange = 1;
    @Setter @Getter @Accessors(chain = true)
    protected boolean shadow = true;
    @Setter @Getter @Accessors(chain = true)
    protected int maxLineWidth = 200;
    @Setter @Getter @Accessors(chain = true)
    protected int backgroundColor;

    @Setter @Getter @Accessors(chain = true)
    protected boolean seeThroughBlocks = false;
    @Setter @Getter @Accessors(chain = true)
    protected TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    @Setter @Getter @Accessors(chain = true)
    protected byte textOpacity = (byte) -1;
    @Getter @Accessors(chain = true)
    public final RenderMode renderMode;


    @Getter @Accessors(chain = true)
    private Location location;

    /**
     * Only these player will see the bubble if RenderMode was set to VIEWER_LIST
     */
    @Getter
    protected List<Player> viewers = new ArrayList<>();

    @Getter
    protected boolean dead = false;

    @Getter
    private BukkitTask task;

    public TextHologram(String id, RenderMode renderMode) {
        this.renderMode = renderMode;
        if(id.contains(" ")) throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        this.id = id.toLowerCase();
        startRunnable();
    }

    public TextHologram(String id) {
        this.renderMode = RenderMode.NEARBY;
        if(id.contains(" ")) throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        this.id = id.toLowerCase();
    }

    private void startRunnable() {
        if(task != null) return;
        task = Bukkit.getServer().getScheduler().runTaskTimer(HologramAPI.getInstance(), this::updateAffectedPlayers, 20L, 20L * 2);
    }

    public TextHologram spawn(Location location) {
        this.location = location;
        entityID = ThreadLocalRandom.current().nextInt(4000, Integer.MAX_VALUE);
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(entityID, Optional.of(UUID.randomUUID()),
                EntityTypes.TEXT_DISPLAY,
                new Vector3d(this.location.getX(), this.location.getY() + 1, this.location.getZ()), 0f, 0f, 0f, 0, Optional.empty());
        updateAffectedPlayers();
        sendPacket(packet);
        return this;
    }

    public TextHologram update() {
        updateAffectedPlayers();
        TextDisplayMeta meta = (TextDisplayMeta) EntityMeta.createMeta(this.entityID, EntityTypes.TEXT_DISPLAY);
        meta.setText(getTextAsComponent());
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.getInterpolationDurationTicks());
        meta.setPositionRotationInterpolationDuration(this.getInterpolationDurationTicks());
        meta.setTranslation(new com.github.retrooper.packetevents.util.Vector3f(this.getTranslation().getX(), this.getTranslation().getY() ,this.getTranslation().getZ()));
        meta.setScale(new com.github.retrooper.packetevents.util.Vector3f(this.getScale().getX(), this.getScale().getY() ,this.getScale().getZ()));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.getBillboard().name()));
        meta.setLineWidth(this.getMaxLineWidth());
        meta.setViewRange((float) this.getViewRange());
        meta.setBackgroundColor(this.getBackgroundColor());
        meta.setTextOpacity(this.getTextOpacity());
        meta.setShadow(this.isShadow());
        meta.setSeeThrough(this.isSeeThroughBlocks());
        switch (this.getAlignment()) {
            case LEFT -> meta.setAlignLeft(true);
            case RIGHT -> meta.setAlignRight(true);
        }
        sendPacket(meta.createPacket());
        return this;
    }

    public TextHologram kill() {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.dead = true;
        return this;
    }

    public TextHologram teleport(Location location) {
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false);
        this.location = location;
        sendPacket(packet);
        return this;
    }

    public void addAllViewers(List<Player> viewerList) {
        this.viewers.addAll(viewerList);
    }

    public void addViewer(Player player) {
        this.viewers.add(player);
    }

    public void removeViewer(Player player) {
        this.viewers.remove(player);
    }

    public void removeAllViewers() {
        this.viewers.clear();
    }

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public TextHologram setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        return this;
    }


    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }


    public TextHologram setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        return this;
    }

    public Component getTextAsComponent() {
        return this.text;
    }

    public String getText() {
        return ((TextComponent)this.text).content();
    }

    public String getTextWithoutColor() {
        return ChatColor.stripColor(getText());
    }

    public TextHologram setText(String text) {
        this.text = Component.text(replaceFontImages(text));
        return this;
    }

    public TextHologram setText(Component component) {
        this.text = component;
        return this;
    }

    public TextHologram setMiniMessageText(String text) {
        this.text = MiniMessage.get(replaceFontImages(text));
        return this;
    }

    private String replaceFontImages(String string) {
        return HologramAPI.getReplaceText().replace(string);
    }

    private void updateAffectedPlayers() {

        if(!this.getViewers().isEmpty()) this.getViewers().forEach(player1 -> {
            if(player1.isOnline() && player1.getWorld() != this.location.getWorld() || player1.getLocation().distance(this.location) > 20) {
                WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
                if(player1.isOnline()) HologramAPI.getPlayerManager().sendPacket(player1, packet);
            }
        });

        if(this.getRenderMode() == RenderMode.VIEWER_LIST) return;
        if(this.getRenderMode() == RenderMode.ALL) this.addAllViewers(new ArrayList<>(Bukkit.getOnlinePlayers()));
        if(this.getRenderMode() == RenderMode.NEARBY) this.location.getWorld().getNearbyEntities(this.location, 40, 40, 40).forEach(entity -> {
            if(entity instanceof Player pl) this.getViewers().add(pl);
        });
    }

    private void sendPacket(PacketWrapper<?> packet) {
        if(this.renderMode == RenderMode.NONE) return;
        this.getViewers().forEach(player -> {
            HologramAPI.getPlayerManager().sendPacket(player, packet);
        });
    }

}
