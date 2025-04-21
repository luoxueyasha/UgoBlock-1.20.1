package com.iwaliner.ugoblock.object.seat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class SeatEntity extends Entity {
    public static final EntityDataAccessor<Boolean> DATA_ROTATING_ID = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));
    public SeatEntity(EntityType<?> p_270360_, Level p_270280_) {
        super(Register.SeatEntity.get(), p_270280_);
    }
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    public SeatEntity(Level level,BlockPos pos,boolean flag) {
        super(Register.SeatEntity.get(), level);
        this.setPos(pos.getX()+0.5D, pos.getY(), pos.getZ()+0.5D);
        this.noPhysics = false;
        this.noCulling = false;
        this.blocksBuilding=false;
        this.entityData.set(DATA_ROTATING_ID,flag);
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ROTATING_ID,false);
    }
    @Override
    public  Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("rotatable")) {
            this.entityData.set(DATA_ROTATING_ID,tag.getBoolean("rotatable"));
        }
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("rotatable",entityData.get(DATA_ROTATING_ID));
    }
    public boolean isRotating(){
        return entityData.get(DATA_ROTATING_ID);
    }
    public void setRotating(boolean flag){
        entityData.set(DATA_ROTATING_ID,flag);
    }
    @Override
    public void tick() {
        super.tick();
        this.tickLerp();
        if (level().isClientSide) {
            return;
        }
        if(!(level().getBlockState(blockPosition()).getBlock() instanceof SeatBlock)&&!isPassenger()){
            discard();
        }
        AABB axisalignedbb =this.getBoundingBox();
        List<LivingEntity> list =level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if(!level().isClientSide()&&this.getPassengers().isEmpty()){
            if(!list.isEmpty()) {
                for (LivingEntity entity : list) {
                   if(!entity.isPassenger()&&!entity.isSuppressingBounce()&&!(entity instanceof Player)&&!isPassenger()) {
                        entity.startRiding(this);
                        level().playSound((Player) null, this.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return;
                              }
                }
            }
        }
        if(!isRotating()) {
        if ( level().getBlockState(blockPosition()).getBlock() instanceof SeatBlock) {
            return;
        }
        this.discard();
        }
    }
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
    @Override
    public double getPassengersRidingOffset() {
        return  -0.2d;
    }
    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }
    /**trueにすると、当たり判定内に入ったときにはじき出される*/
    public boolean canBeCollidedWith() {
        return false;
    }
    public boolean isPickable() {
        return true;
    }
    @Override
    public boolean mayInteract(Level level, BlockPos pos) {
        return true;
    }
    @Override
    protected boolean canRide(Entity p_20339_) {
        return true;
    }
    @Override
    public boolean canRiderInteract() {
        return true;
    }
    /**右クリック時の処理*/
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(hand==InteractionHand.OFF_HAND){
            return InteractionResult.FAIL;
        }
        if (!this.level().isClientSide()&&this.getPassengers().isEmpty())
        {

            player.startRiding(this);
            level().playSound((Player) null, this.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean hurt(DamageSource p_19946_, float p_19947_) {
        if(level().getBlockState(blockPosition()).is(Register.seat.get())){
            level().destroyBlock(blockPosition(),true);
        }
        discard();
        return true;
    }
    @Override
    public void lerpTo(double p_297677_, double p_301293_, double p_301384_, float p_300635_, float p_299108_, int p_299659_,boolean b) {
        this.lerpX = p_297677_;
        this.lerpY = p_301293_;
        this.lerpZ = p_301384_;
        this.lerpYRot = (double)p_300635_;
        this.lerpXRot = (double)p_299108_;
        this.lerpSteps = 10;
     }
    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
        }
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
    public Vec3 getDismountLocationForPassenger(LivingEntity p_38145_) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(p_38145_);
        } else {
            int[][] aint = DismountHelper.offsetsForDirection(direction);
            BlockPos blockpos = this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            ImmutableList<Pose> immutablelist = p_38145_.getDismountPoses();

            for(Pose pose : immutablelist) {
                EntityDimensions entitydimensions = p_38145_.getDimensions(pose);
                float f = Math.min(entitydimensions.width, 1.0F) / 2.0F;

                for(int i : POSE_DISMOUNT_HEIGHTS.get(pose)) {
                    for(int[] aint1 : aint) {
                        blockpos$mutableblockpos.set(blockpos.getX() + aint1[0], blockpos.getY() + i, blockpos.getZ() + aint1[1]);
                        double d0 = this.level().getBlockFloorHeight(DismountHelper.nonClimbableShape(this.level(), blockpos$mutableblockpos), () -> {
                            return DismountHelper.nonClimbableShape(this.level(), blockpos$mutableblockpos.below());
                        });
                        if (DismountHelper.isBlockFloorValid(d0)) {
                            AABB aabb = new AABB((double)(-f), 0.0D, (double)(-f), (double)f, (double)entitydimensions.height, (double)f);
                            Vec3 vec3 = Vec3.upFromBottomCenterOf(blockpos$mutableblockpos, d0);
                            if (DismountHelper.canDismountTo(this.level(), p_38145_, aabb.move(vec3))) {
                                p_38145_.setPose(pose);
                                return vec3;
                            }
                        }
                    }
                }
            }
            double d1 = this.getBoundingBox().maxY;
            blockpos$mutableblockpos.set((double)blockpos.getX(), d1, (double)blockpos.getZ());
            for(Pose pose1 : immutablelist) {
                double d2 = (double)p_38145_.getDimensions(pose1).height;
                int j = Mth.ceil(d1 - (double)blockpos$mutableblockpos.getY() + d2);
                double d3 = DismountHelper.findCeilingFrom(blockpos$mutableblockpos, j, (p_289495_) -> {
                    return this.level().getBlockState(p_289495_).getCollisionShape(this.level(), p_289495_);
                });
                if (d1 + d2 <= d3) {
                    p_38145_.setPose(pose1);
                    break;
                }
            }

            return super.getDismountLocationForPassenger(p_38145_);
        }
    }
}
