package com.iwaliner.ugoblock.object.moving_block;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CollisionEntity extends Entity {
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(CollisionEntity .class, EntityDataSerializers.BLOCK_STATE);

    public CollisionEntity(EntityType<?> type, Level level) {
        super(Register.CollisionEntity.get(), level);
    }
    public CollisionEntity(Level level, double x, double y, double z,BlockState state) {
        super(Register.CollisionEntity.get(), level);
        this.setPos(x, y, z);
        this.entityData.set(DATA_BLOCK_STATE_ID,state);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    public void setBlockState(BlockState p_270267_) {
        this.entityData.set(DATA_BLOCK_STATE_ID, p_270267_);
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_BLOCK_STATE_ID, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("block_state")));

    }



    /**trueにすると、当たり判定内に入ったときにはじき出される*/
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
    }

    @Override
    public void tick() {
        Level level=level();
        BlockPos pos=blockPosition();
        BlockState state=entityData.get(DATA_BLOCK_STATE_ID);
        //if(tickCount>2){
            if(!state.isAir()) {
                level.setBlockAndUpdate(pos, state);
            }
            discard();
       // }

    }
}
