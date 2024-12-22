package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.utils.Vector3F;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Hologram {
    @Getter
    protected final List<Player> viewers = new CopyOnWriteArrayList<>();

    @Getter
    protected Location location;

    @Getter
    protected boolean dead = true;

    @Getter @Accessors(chain = true)
    protected long updateTaskPeriod = 20L * 3;

    @Getter @Accessors(chain = true)
    protected double nearbyEntityScanningDistance = 30.0;

    @Setter
    @Getter @Accessors(chain = true)
    protected Display.Billboard billboard = Display.Billboard.CENTER;

    @Setter @Getter @Accessors(chain = true)
    protected int interpolationDurationRotation = 10;

    @Setter @Getter @Accessors(chain = true)
    protected int interpolationDurationTransformation = 10;

    @Setter @Getter @Accessors(chain = true)
    protected double viewRange = 1.0;

    @Getter
    protected final String id;

    @Getter @Accessors(chain = true)
    protected int entityID;

    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f translation = new Vector3f(0, 0F, 0);

    protected Quaternion4f rightRotation = new Quaternion4f(0, 0, 0, 1);
    protected Quaternion4f leftRotation = new Quaternion4f(0, 0, 0, 1);

    @Getter
    protected final RenderMode renderMode;

    protected final EntityType entityType;

    @Getter
    protected BukkitTask task;

    @Getter
    /**
     * Do not use this if you don't know what you are doing!
     * this interface for accessing specific setters is only for internal methods.
     */
    private Internal internalAccess;

    public interface Internal {
        /**
         * Use Hologram#telport(Location) if you want to move the hologram instead!
         * @param location
         */
        Hologram setLocation(Location location);
        Hologram setDead(boolean dead);
        Hologram setEntityId(int entityId);
        Hologram sendPacket(PacketWrapper<?> packet);
        Hologram updateAffectedPlayers();
    }

    protected Hologram(String id, RenderMode renderMode, EntityType entityType) {
        this.entityType = entityType;
        validateId(id);
        this.id = id.toLowerCase();
        this.renderMode = renderMode;
        startRunnable();
    }

    protected Hologram(String id, EntityType entityType) {
        this(id, RenderMode.NEARBY, entityType);
        startRunnable();
    }

    private void startRunnable() {
        if (task != null) return;
        task = Bukkit.getServer().getScheduler().runTaskTimer(HologramLib.getInstance(), this::updateAffectedPlayers, 60L, updateTaskPeriod);
    }

    private class InternalSetters implements Internal {
        @Override
        public Hologram setLocation(Location location) {
            if (location == null) {
                throw new IllegalArgumentException("Location cannot be null");
            }
            Hologram.this.location = location;
            return Hologram.this;
        }

        @Override
        public Hologram setDead(boolean dead) {
            Hologram.this.dead = dead;
            return Hologram.this;
        }

        @Override
        public Hologram setEntityId(int entityId) {
            Hologram.this.entityID = entityId;
            return Hologram.this;
        }

        @Override
        public Hologram sendPacket(PacketWrapper<?> packet) {
            Hologram.this.sendPacket(packet);
            return Hologram.this;
        }

        @Override
        public Hologram updateAffectedPlayers() {
            Hologram.this.updateAffectedPlayers();
            return Hologram.this;
        }
    }


    /**
     * Sends update packets to all viewers.
     * Should be called after making any changes to the hologram object.
     */
    public Hologram update() {
        Bukkit.getServer().getScheduler().runTask(HologramLib.getInstance(), () -> {
            updateAffectedPlayers();
            sendPacket(createMeta());
        });
        return this;
    }

    protected void validateId(String id) {
        if (id.contains(" ")) {
            throw new IllegalArgumentException("The hologram ID cannot contain spaces! (" + id + ")");
        }
    }

    protected com.github.retrooper.packetevents.util.Vector3f toVector3f(Vector3f vector) {
        return new com.github.retrooper.packetevents.util.Vector3f(vector.x, vector.y, vector.z);
    }

    /**
     * Use HologramManager#remove(Hologram.class); instead!
     * Only if you want to manage the holograms yourself and don't want to use the animation system use this
     */
    public void kill() {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
        sendPacket(packet);
        this.dead = true;
    }

    public Hologram teleport(Location location) {
        WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(this.entityID, SpigotConversionUtil.fromBukkitLocation(location), false);
        this.location = location;
        sendPacket(packet);
        return this;
    }

    protected abstract WrapperPlayServerEntityMetadata createMeta();

    public Vector3F getTranslation() {
        return new Vector3F(this.translation.x, this.translation.y, this.translation.z);
    }

    public Hologram setLeftRotation(float x, float y, float z, float w) {
        this.leftRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public Hologram setRightRotation(float x, float y, float z, float w) {
        this.rightRotation = new Quaternion4f(x, y, z, w);
        return this;
    }

    public Hologram setTranslation(float x, float y, float z) {
        this.translation = new Vector3f(x, y, z);
        return this;
    }

    public Hologram setTranslation(Vector3F translation) {
        this.translation = new Vector3f(translation.x, translation.y, translation.z);
        return this;
    }

    public Vector3F getScale() {
        return new Vector3F(this.scale.x, this.scale.y, this.scale.z);
    }

    public abstract Hologram setScale(float x, float y, float z);

    public abstract Hologram setScale(Vector3F scale);

    private void updateAffectedPlayers() {
        List<Player> newPlayers = new ArrayList<>();
        List<Player> toRemove = viewers.stream()
                .filter(player -> player.isOnline() && (player.getWorld() != this.location.getWorld() || player.getLocation().distance(this.location) > 20))
                .peek(player -> {
                    WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(this.entityID);
                    HologramLib.getInstance().getPlayerManager().sendPacket(player, packet);
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
                    this.entityID, Optional.of(UUID.randomUUID()), this.entityType,
                    new Vector3d(location.getX(), location.getY() + 1, location.getZ()), 0f, 0f, 0f, 0, Optional.empty()
            );
            this.sendPacket(packet, newPlayers);
            this.sendPacket(createMeta(), newPlayers);
        }
        this.viewers.addAll(newPlayers);
    }

    /**
     * Attaches this hologram to another entity, making it ride the target entity.
     *
     * @param textHologram The hologram to attach
     * @param entityID The entity ID to attach the hologram to
     * @param persistent If the hologram should be re-attached automatically or not TODO
     */
    public void attach(TextHologram textHologram, int entityID, boolean persistent) {
        int[] hologramToArray = { textHologram.getEntityID() };
        WrapperPlayServerSetPassengers attachPacket = new WrapperPlayServerSetPassengers(entityID, hologramToArray);
        Bukkit.getServer().getScheduler().runTask(HologramLib.getInstance(), () -> {
            sendPacket(attachPacket);
        });
    }


    public Hologram addViewer(Player player) {
        this.viewers.add(player);
        return this;
    }

    public Hologram removeViewer(Player player) {
        this.viewers.remove(player);
        return this;
    }

    public Hologram addAllViewers(List<Player> viewerList) {
        this.viewers.addAll(viewerList);
        return this;
    }

    public Hologram removeAllViewers() {
        this.viewers.clear();
        return this;
    }

    protected abstract Hologram copy();

    protected abstract Hologram copy(String id);

    protected void sendPacket(PacketWrapper<?> packet) {
        if (this.renderMode == RenderMode.NONE) return;
        viewers.forEach(player -> HologramLib.getInstance().getPlayerManager().sendPacket(player, packet));
    }

    protected void sendPacket(PacketWrapper<?> packet, List<Player> players) {
        if (this.renderMode == RenderMode.NONE) return;
        players.forEach(player -> HologramLib.getInstance().getPlayerManager().sendPacket(player, packet));
    }
}