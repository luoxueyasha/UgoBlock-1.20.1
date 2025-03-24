package com.iwaliner.ugoblock.mixin;

import com.iwaliner.ugoblock.object.moving_block.CollisionEntity;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow public abstract AABB getBoundingBox();

    @Shadow @Final @Deprecated private EntityType<?> type;

    @Inject(method = "isNoGravity",at = @At("HEAD"), cancellable = true)
    private void isNoGravityInject(CallbackInfoReturnable<Boolean> cir){
        for (Entity entity : level().getEntities((Entity) null,getBoundingBox(), (o) -> {
            return (o instanceof MovingBlockEntity);
        })) {
            cir.setReturnValue(true);
        }
    }
}
