package com.iwaliner.ugoblock.mixin;

import com.iwaliner.ugoblock.object.MovingBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow public abstract AABB getBoundingBox();

    @Inject(method = "isNoGravity",at = @At("HEAD"), cancellable = true)
    private void isNoGravityInject(CallbackInfoReturnable<Boolean> cir){
        for (Entity entity : level().getEntities((Entity) null,getBoundingBox(), (o) -> {
            return (o instanceof MovingBlockEntity);
        })) {
            cir.setReturnValue(true);
        }
    }
}
