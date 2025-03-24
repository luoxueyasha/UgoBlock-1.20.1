package com.iwaliner.ugoblock.mixin;

import com.iwaliner.ugoblock.object.gravitate_block.GravitatePistonBaseBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {

    @Shadow public abstract boolean isExtending();

    @Shadow public abstract boolean isSourcePiston();

    @Shadow public abstract BlockState getMovedState();

    @Shadow private float progress;

    @Shadow private BlockState movedState;

    @Inject(method = "getCollisionRelatedBlockState",at = @At("HEAD"), cancellable = true)
    private void getCollisionRelatedBlockStateInject(CallbackInfoReturnable<BlockState> cir){
        if(!isExtending()&&isSourcePiston()&&this.movedState.getBlock() instanceof GravitatePistonBaseBlock){
          BlockState headState=  Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.progress > 0.25F)).setValue(PistonHeadBlock.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT).setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING));
        cir.setReturnValue(headState);
        }
    }
}
