package com.iwaliner.ugoblock.object.gravitate_block;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

public class GravitatePistonHeadBlock extends PistonHeadBlock {
    public GravitatePistonHeadBlock(Properties p_60259_) {
        super(p_60259_);
    }
    private boolean isFittingBase(BlockState p_60298_, BlockState p_60299_) {
        Block block = Register.gravitate_piston_base_block.get();
        return p_60299_.is(block) && p_60299_.getValue(PistonBaseBlock.EXTENDED) && p_60299_.getValue(FACING) == p_60298_.getValue(FACING);
    }

    public void playerWillDestroy(Level p_60265_, BlockPos p_60266_, BlockState p_60267_, Player p_60268_) {
        if (!p_60265_.isClientSide && p_60268_.getAbilities().instabuild) {
            BlockPos blockpos = p_60266_.relative(p_60267_.getValue(FACING).getOpposite());
            if (this.isFittingBase(p_60267_, p_60265_.getBlockState(blockpos))) {
                p_60265_.destroyBlock(blockpos, false);
            }
        }

        super.playerWillDestroy(p_60265_, p_60266_, p_60267_, p_60268_);
    }
    public ItemStack getCloneItemStack(BlockGetter p_60261_, BlockPos p_60262_, BlockState p_60263_) {
        return new ItemStack(Register.gravitate_piston_base_block.get());
    }
    public boolean canSurvive(BlockState p_60288_, LevelReader p_60289_, BlockPos p_60290_) {
        BlockState blockstate = p_60289_.getBlockState(p_60290_.relative(p_60288_.getValue(FACING).getOpposite()));
        return this.isFittingBase(p_60288_, blockstate) || blockstate.is(Blocks.MOVING_PISTON) && blockstate.getValue(FACING) == p_60288_.getValue(FACING);
    }

}
