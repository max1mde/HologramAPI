package com.maximde.hologramapi.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class TextHologram {

    @Getter @Accessors(chain = true)
    private long updateTaskPeriod = 20L * 3;
    @Getter @Accessors(chain = true)
    private double nearbyEntityScanningDistance = 30.0;
    @Getter
    private final String id;

    @Getter @Accessors(chain = true)
    private int entityID;

    protected Component text = Component.text("Hologram API");
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    @Setter @Getter @Accessors(chain = true)
    private Display.Billboard billboard = Display.Billboard.CENTER;
    @Setter @Getter @Accessors(chain = true)
    private int interpolationDurationRotation = 10;
    @Setter @Getter @Accessors(chain = true)
    private int interpolationDurationTransformation = 10;
    @Setter @Getter @Accessors(chain = true)
    private double viewRange = 1.0;
    @Setter @Getter @Accessors(chain = true)
    private boolean shadow = true;
    @Setter @Getter @Accessors(chain = true)
    private int maxLineWidth = 200;
    @Setter @Getter @Accessors(chain = true)
    private int backgroundColor;
    @Setter @Getter @Accessors(chain = true)
    private boolean seeThroughBlocks = false;
    @Setter @Getter @Accessors(chain = true)
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;
    @Setter @Getter @Accessors(chain = true)
    private byte textOpacity = (byte) -1;

    @Getter @Accessors(chain = true)
    private final RenderMode renderMode;

    @Getter @Accessors(chain = true)
    private Location location;

    @Getter
    private final List<Player> viewers = new CopyOnWriteArrayList<>();

    @Getter
    private boolean dead = true;

    @Getter
    private BukkitTask task;

    @Getter
    /**
     * Do not use this if you don't know what you are doing!
     * this interface for accessing specific setters is only for internal methods.
     */
    private Internal internalAccess;

    public interface Internal {
        /**
         * Use TextHologram#telport(Location) if you want to move the hologram instead!
         * @param location
         */
        TextHologram setLocation(Location location);
        TextHologram setDead(boolean dead);
        TextHologram setEntityId(int entityId);
        TextHologram sendPacket(PacketWrapper<?> packet);
        TextHologram updateAffectedPlayers();
        TextHologram copy(String id);
        TextHologram copy();
    }


    /**
     * Creates a new text hologram with the specified ID and render mode.
     *
     * @param id Unique identifier for this hologram. Cannot contain spaces.
     * @param renderMode Determines how and to whom the hologram is rendered
     * @throws IllegalArgumentException if id contains spaces
     */
    public TextHologram(String id, RenderMode renderMode) {
        this.renderMode = renderMode;
        validateId(id);
        this.id = id.toLowerCase();
        startRunnable();
        this.internalAccess = new InternalSetters();
    }

    /**
     * Creates a new text hologram with the specified ID and nearby render mode.
     *
     * @param id Unique identifier for this hologram. Cannot contain spaces.
     * @throws IllegalArgumentException if id contains spaces
     */
    public TextHologram(String id) {
        this(id, RenderMode.NEARBY);
        this.internalAccess = new InternalSetters();
    }

    /**
     * Creates a copy of this hologram with a new ID.
     * The new ID will be the original ID with '_copy_<random number>' appended.
     * @return A new TextHologram instance with copied properties
     */
    private TextHologram copy() {
        int randomNumber = ThreadLocalRandom.current().nextInt(100000);
        return this.copy(this.id + "_copy_" + randomNumber);
    }

    /**
     * Creates a copy of this hologram with a new ID.
     *
     * @return A new TextHologram instance with copied properties
     */
    private TextHologram copy(String id) {
        TextHologram copy = new TextHologram(id, this.renderMode);
        copy.text = this.text;
        copy.scale = new Vector3f(this.scale);
        copy.translation = new Vector3f(this.translation);
        copy.rightRotation = new Quaternion4f(this.rightRotation.getX(), this.rightRotation.getY(),
                this.rightRotation.getZ(), this.rightRotation.getW());
        copy.leftRotation = new Quaternion4f(this.leftRotation.getX(), this.leftRotation.getY(),
                this.leftRotation.getZ(), this.leftRotation.getW());
        copy.billboard = this.billboard;
        copy.interpolationDurationRotation = this.interpolationDurationRotation;
        copy.interpolationDurationTransformation = this.interpolationDurationTransformation;
        copy.viewRange = this.viewRange;
        copy.shadow = this.shadow;
        copy.maxLineWidth = this.maxLineWidth;
        copy.backgroundColor = this.backgroundColor;
        copy.seeThroughBlocks = this.seeThroughBlocks;
        copy.alignment = this.alignment;
        copy.textOpacity = this.textOpacity;
        copy.updateTaskPeriod = this.updateTaskPeriod;
        copy.nearbyEntityScanningDistance = this.nearbyEntityScanningDistance;
        return copy;
    }

    private class InternalSetters implements Internal {
        @Override
        public TextHologram setLocation(Location location) {
            if (location == null) {
                throw new IllegalArgumentException("Location cannot be null");
            }
            TextHologram.this.location = location;
            return TextHologram.this;
        }

        @Override
        public TextHologram setDead(boolean dead) {
            TextHologram.this.dead = dead;
            return TextHologram.this;
        }

        @Override
        public TextHologram setEntityId(int entityId) {
            TextHologram.this.entityID = entityId;
            return TextHologram.this;
        }

        @Override
        public TextHologram sendPacket(PacketWrapper<?> packet) {
            TextHologram.this.sendPacket(packet);
            return TextHologram.this;
        }

        @Override
        public TextHologram updateAffectedPlayers() {
            TextHologram.this.updateAffectedPlayers();
            return TextHologram.this;
        }

        @Override
        public TextHologram copy(String id) { TextHologram.this.copy(id); return TextHologram.this;}

        @Override
        public TextHologram copy() { TextHologram.this.copy(); return TextHologram.this;}
    }

    private void validateId(String id) {
        if (id.contains(" ")) {
            throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        }
    }

    private void startRunnable() {
        if (task != null) return;
        task = Bukkit.getServer().getScheduler().runTaskTimer(HologramAPI.getInstance(), this::updateAffectedPlayers, 60L, updateTaskPeriod);
    }

    /**
     * Attaches this hologram to another entity, making it ride the target entity.
     *
     * @param textHologram The hologram to attach
     * @param entityID The entity ID to attach the hologram to
     */
    public void attach(TextHologram textHologram, int entityID) {
        int[] hologramToArray = { textHologram.getEntityID() };
        WrapperPlayServerSetPassengers attachPacket = new WrapperPlayServerSetPassengers(entityID, hologramToArray);
        Bukkit.getServer().getScheduler().runTask(HologramAPI.getInstance(), () -> {
            sendPacket(attachPacket);
        });
    }

    /**
     * Sends update packets to all viewers.
     * Should be called after making any changes to the hologram object.
     */
    public TextHologram update() {
        Bukkit.getServer().getScheduler().runTask(HologramAPI.getInstance(), () -> {
            updateAffectedPlayers();
            TextDisplayMeta meta = createMeta();
            sendPacket(meta.createPacket());
        });
        return this;
    }

    private TextDisplayMeta createMeta() {
        TextDisplayMeta meta = (TextDisplayMeta) EntityMeta.createMeta(this.entityID, EntityTypes.TEXT_DISPLAY);
        meta.setText(getTextAsComponent());
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(toVector3f(this.translation));
        meta.setScale(toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setLineWidth(this.maxLineWidth);
        meta.setViewRange((float) this.viewRange);
        meta.setBackgroundColor(this.backgroundColor);
        meta.setTextOpacity(this.textOpacity);
        meta.setShadow(this.shadow);
        meta.setSeeThrough(this.seeThroughBlocks);
        setInternalAlignment(meta);
        return meta;
    }

    private void setInternalAlignment(TextDisplayMeta meta) {
        switch (this.alignment) {
            case LEFT -> meta.setAlignLeft(true);
            case RIGHT -> meta.setAlignRight(true);
        }
    }

    private com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

    /**
     * Use HologramManager#remove(TextHologram.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void kill() {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.dead = true;
    }

    public TextHologram teleport(Location location) {
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false);
        this.location = location;
        sendPacket(packet);
        return this;
    }

    public TextHologram addAllViewers(List<Player> viewerList) {
        this.viewers.addAll(viewerList);
        return this;
    }

    public TextHologram addViewer(Player player) {
        this.viewers.add(player);
        return this;
    }

    public TextHologram removeViewer(Player player) {
        this.viewers.remove(player);
        return this;
    }

    public TextHologram removeAllViewers() {
        this.viewers.clear();
        return this;
    }

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public TextHologram setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public TextHologram setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public TextHologram setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return this;
    }

    public TextHologram setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        return this;
    }

    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    public TextHologram setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public TextHologram setScale(Vector3F scale) {
        this.scale = new Vector3f(scale.x, scale.y, scale.z);
        return this;
    }

    public Component getTextAsComponent() {
        return this.text;
    }

    public String getText() {
        return ((TextComponent) this.text).content();
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
        return HologramAPI.getInstance().getReplaceText().replace(string);
    }

    private void updateAffectedPlayers() {
        List<Player> newPlayers = new ArrayList<>();
        List<Player> toRemove = viewers.stream()
                .filter(player -> player.isOnline() && (player.getWorld() != this.location.getWorld() || player.getLocation().distance(this.location) > 20))
                .peek(player -> {
                    WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
                    HologramAPI.getInstance().getPlayerManager().sendPacket(player, packet);
                })
                .toList();
        viewers.removeAll(toRemove);


        if (this.renderMode == RenderMode.VIEWER_LIST) return;

        if (this.renderMode == RenderMode.ALL) {
            newPlayers.addAll(new ArrayList<>(Bukkit.getOnlinePlayers()));
        } else if (this.renderMode == RenderMode.NEARBY) {
            this.location.getWorld().getNearbyEntities(this.location, nearbyEntityScanningDistance, nearbyEntityScanningDistance, nearbyEntityScanningDistance)
                    .stream()
                    .filter(entity -> entity instanceof Player)
                    .forEach(entity -> newPlayers.add((Player) entity));
        }
        newPlayers.removeAll(this.viewers);
        if(!dead && entityID != 0) {
            WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                    this.entityID, Optional.of(UUID.randomUUID()), EntityTypes.TEXT_DISPLAY,
                    new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
            );
            sendPacket(packet, newPlayers);
            sendPacket(createMeta().createPacket(), newPlayers);
        }
        this.viewers.addAll(newPlayers);
    }

    private void sendPacket(PacketWrapper<?> packet) {
        if (this.renderMode == RenderMode.NONE) return;
        viewers.forEach(player -> HologramAPI.getInstance().getPlayerManager().sendPacket(player, packet));
    }

    private void sendPacket(PacketWrapper<?> packet, List<Player> players) {
        if (this.renderMode == RenderMode.NONE) return;
        players.forEach(player -> HologramAPI.getInstance().getPlayerManager().sendPacket(player, packet));
    }
}
