package com.iwaliner.ugoblock.object.block_imitation_wand;

import net.minecraft.world.level.block.state.BlockState;

public interface ImitatableBlockEntity {
    public BlockState getImitatingState();
    public void setImitatingState(BlockState state);
}
