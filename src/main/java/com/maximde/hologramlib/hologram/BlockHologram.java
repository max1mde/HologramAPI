package com.maximde.hologramlib.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.maximde.hologramlib.utils.Vector3F;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.BlockDisplayMeta;
import org.joml.Vector3f;

import java.awt.*;


public class BlockHologram extends Hologram<BlockHologram> {

    @Getter
    @Accessors(chain = true)
    protected int block = 0;

    @Getter
    @Accessors(chain = true)
    protected boolean onFire = false;

    @Getter
    @Accessors(chain = true)
    protected boolean glowing = false;

    @Getter
    @Accessors(chain = true)
    protected int glowColor = Color.YELLOW.getRGB();


    protected BlockHologram(String id, RenderMode renderMode) {
        super(id, renderMode, EntityTypes.BLOCK_DISPLAY);
    }

    protected BlockHologram(String id, EntityType entityType) {
        super(id, entityType);
    }

    @Override
    protected WrapperPlayServerEntityMetadata createMeta() {
        BlockDisplayMeta meta = (BlockDisplayMeta) EntityMeta.createMeta(super.entityID, EntityTypes.BLOCK_DISPLAY);
        meta.setInterpolationDelay(-1);
        meta.setTransformationInterpolationDuration(this.interpolationDurationTransformation);
        meta.setPositionRotationInterpolationDuration(this.interpolationDurationRotation);
        meta.setTranslation(super.toVector3f(this.translation));
        meta.setScale(super.toVector3f(this.scale));
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(this.billboard.name()));
        meta.setViewRange((float) this.viewRange);
        meta.setBlockId(this.block);
        meta.setOnFire(this.onFire);
        meta.setGlowing(this.glowing);
        meta.setGlowColorOverride(this.glowColor);
        return meta.createPacket();
    }

    @Override
    protected BlockHologram copy() {
        return null;
    }

    @Override
    protected BlockHologram copy(String id) {
        return null;
    }
}