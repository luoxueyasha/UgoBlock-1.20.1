package com.iwaliner.ugoblock.object.moving_block;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CollisionEntity extends Entity {
    public CollisionEntity(EntityType<?> type, Level level) {
        super(Register.CollisionEntity.get(), level);
        this.noPhysics = false;
        this.noCulling = false;
        this.blocksBuilding=false;
    }
    public CollisionEntity(Level level, double x, double y, double z) {
        super(Register.CollisionEntity.get(), level);
        this.setPos(x, y, z);
        this.noPhysics = false;
        this.noCulling = false;
        this.blocksBuilding=false;
    }
     @Override
    protected void defineSynchedData() {
      }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }
    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    /**trueにすると、当たり判定内に入ったときにはじき出される*/
    public boolean canBeCollidedWith() {
        return true;
    }
    public boolean isPushable() {
        return false;
    }
    public boolean isPickable() {
        return true;
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
     }
    @Override
    public void tick() {
                discard();
    }
}
