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
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(CollisionEntity .class, EntityDataSerializers.BLOCK_STATE);

    private static final EntityDataAccessor<CompoundTag> DATA_BLOCKENTITY_ID = SynchedEntityData.defineId(CollisionEntity .class, EntityDataSerializers.COMPOUND_TAG);

    public CollisionEntity(EntityType<?> type, Level level) {
        super(Register.CollisionEntity.get(), level);
        this.noPhysics = false;
        this.noCulling = false;
        this.blocksBuilding=false;
    }
    public CollisionEntity(Level level, double x, double y, double z,BlockState state,CompoundTag blockEntityData) {
        super(Register.CollisionEntity.get(), level);
        this.setPos(x, y, z);
        this.entityData.set(DATA_BLOCK_STATE_ID,state);
        this.entityData.set(DATA_BLOCKENTITY_ID,blockEntityData);
        this.noPhysics = false;
        this.noCulling = false;
        this.blocksBuilding=false;
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    public void setBlockState(BlockState p_270267_) {
        this.entityData.set(DATA_BLOCK_STATE_ID, p_270267_);
    }
    public void setBlockEntityData(CompoundTag tag) {
        this.entityData.set(DATA_BLOCKENTITY_ID, tag);
    }
    public CompoundTag getBlockEntityData(){
        return entityData.get(DATA_BLOCKENTITY_ID);
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_BLOCK_STATE_ID, Blocks.AIR.defaultBlockState());
        this.entityData.define(DATA_BLOCKENTITY_ID, new CompoundTag());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("block_state")));
        this.setBlockEntityData(tag.getCompound("block_entity"));

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
        tag.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
        tag.put("block_entity", entityData.get(DATA_BLOCKENTITY_ID));
    }

    @Override
    public void tick() {
        Level level=level();
        BlockPos pos=blockPosition();
        BlockState state=entityData.get(DATA_BLOCK_STATE_ID);
        //if(tickCount>2){
       /* AABB aabb=new AABB(position().x-0.5D,position().y-0.5D,position().z-0.5D,position().x+0.5D,position().y+0.5D,position().z+0.5D);
        for (Entity entity : level().getEntities((Entity) null,aabb.move(0D,0.5D,0D).inflate(0d, 2d, 0d), (o) -> {
            return o instanceof LivingEntity;
        })) {
            if(entity.getY()<position().y+1D){
                entity.setPos(position().add(0,1.01D,0));
            }
        }*/
            if(!state.isAir()) {
                level.setBlockAndUpdate(pos, state);
                if (!getBlockEntityData().isEmpty() && state.hasBlockEntity()) {
                    if (getBlockEntityData() != null) {
                        BlockEntity blockentity = level.getBlockEntity(pos);

                        if (blockentity != null) {
                            blockentity.load(getBlockEntityData());
                        }
                    }
                }
            }

            //if(tickCount>10) {
                discard();
         //   }
       // }

    }
}
