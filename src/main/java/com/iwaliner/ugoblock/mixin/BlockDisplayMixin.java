package com.iwaliner.ugoblock.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(Display.BlockDisplay.class)
public interface BlockDisplayMixin {
    @Accessor("DATA_BLOCK_STATE_ID")
    public static EntityDataAccessor<BlockState> getData() {
        throw new AssertionError();
    }
}
