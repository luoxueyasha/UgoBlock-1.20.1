package com.iwaliner.ugoblock.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Display.class)
public interface DisplayMixin {

    @Accessor("DATA_INTERPOLATION_DURATION_ID")
    public static EntityDataAccessor<Integer> getDataDuration() {
        throw new AssertionError();
    }

    @Accessor("DATA_INTERPOLATION_START_DELTA_TICKS_ID")
    public static EntityDataAccessor<Integer> getDataStartTick() {
        throw new AssertionError();
    }

    @Accessor("DATA_LEFT_ROTATION_ID")
    public static EntityDataAccessor<Quaternionf> getDataLeftRotation() {
        throw new AssertionError();
    }
    @Accessor("DATA_RIGHT_ROTATION_ID")
    public static EntityDataAccessor<Quaternionf> getDataRightRotation() {
        throw new AssertionError();
    }
}
