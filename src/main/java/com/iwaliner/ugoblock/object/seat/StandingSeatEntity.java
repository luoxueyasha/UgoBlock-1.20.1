package com.iwaliner.ugoblock.object.seat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

import java.util.List;

public class StandingSeatEntity extends Entity {
    public static final EntityDataAccessor<Boolean> DATA_ROTATING_ID = SynchedEntityData.defineId(StandingSeatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ImmutableMap<Pose, ImmutableList<Integer>> POSE_DISMOUNT_HEIGHTS = ImmutableMap.of(Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1));
    public static final EntityDataAccessor<Vector3f> DATA_OFFSET_ID = SynchedEntityData.defineId(StandingSeatEntity.class, EntityDataSerializers.VECTOR3);
    public StandingSeatEntity(EntityType<?> p_270360_, Level p_270280_) {
        super(Register.StandingSeatEntity.get(), p_270280_);
    }
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public StandingSeatEntity(Level level, BlockPos pos, boolean flag) {
        super(Register.StandingSeatEntity.get(), level);
        this.setPos(pos.getX()+0.5D, pos.getY(), pos.getZ()+0.5D);
        this.noPhysics = false;
        this.noCulling = false;
        this.blocksBuilding=false;
        this.entityData.set(DATA_ROTATING_ID,flag);
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ROTATING_ID,false);
        this.entityData.define(DATA_OFFSET_ID,new Vector3f());
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
        if (tag.contains("offsetX")&&tag.contains("offsetY")&&tag.contains("offsetZ")) {
            this.entityData.set(DATA_OFFSET_ID,new Vector3f(tag.getFloat("offsetX"),tag.getFloat("offsetY"),tag.getFloat("offsetZ")));
        }
   }
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("rotatable",entityData.get(DATA_ROTATING_ID));
        tag.putFloat("offsetX",entityData.get(DATA_OFFSET_ID).x);
        tag.putFloat("offsetY",entityData.get(DATA_OFFSET_ID).y);
        tag.putFloat("offsetZ",entityData.get(DATA_OFFSET_ID).z);
    }
    public Vector3f getOffset(){
        return entityData.get(DATA_OFFSET_ID);
    }
    public void setOffset(float x,float y,float z){
        entityData.set(DATA_OFFSET_ID,new Vector3f(x,y,z));
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
        double bigger=0.3D;
        AABB axisalignedbb =this.getBoundingBox();
        List<LivingEntity> list =level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if(!level().isClientSide()&&this.getPassengers().isEmpty()){
            if(!list.isEmpty()) {
                for (LivingEntity entity : list) {
                    if (entity instanceof Player) {
                    } else {
                    if(!entity.isPassenger()&&!entity.isSuppressingBounce()) {
                        entity.startRiding(this);
                        level().playSound((Player) null, this.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return;
                              }
                    }
                }
            }
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
    @Override
    protected void positionRider(Entity entity, MoveFunction moveFunction) {
       int passengerIndex = this.getPassengers().indexOf(entity);
        if(passengerIndex>=0&&entity instanceof Player) {
            Vec3 vec3=this.position();
            double vx=getOffset().x;
            double vy=getOffset().y;
            double vz=getOffset().z;
            double vx0=getOffset().x;
            double vy0=getOffset().y;
            double vz0=getOffset().z;
            if(this.isPassenger()&&this.getVehicle() instanceof MovingBlockEntity movingBlock) {
                boolean flag= movingBlock.getTimeCount() >= movingBlock.getStartTick() && movingBlock.getTimeCount() < movingBlock.getStartTick() + movingBlock.getDuration();
                float thetaDegreeF= (flag||movingBlock.isLoopRotation())? (movingBlock.getTimeCount()-movingBlock.getStartTick())*movingBlock.getDegreeAngle()/(float)movingBlock.getDuration() : movingBlock.getDegreeAngle();
                float degreeCombinedF=thetaDegreeF+movingBlock.getVisualRot()+movingBlock.getStartRotation();
                if(!movingBlock.shouldRotate()){
                    degreeCombinedF=0f;
                }
                List<Vec3> rotatedVec3BasketList= Utils.rotateVec3BasketPosList(movingBlock.getBasketPosList(),BlockPos.ZERO,movingBlock.getActualBlockPos(),movingBlock.getAxis(),degreeCombinedF,movingBlock.getBasketOriginPosList());
                List<Vec3> rotatedVec3List=Utils.rotateVec3PosList(movingBlock.getPosList(),BlockPos.ZERO,movingBlock.getActualBlockPos(),movingBlock.getAxis(),degreeCombinedF);
                rotatedVec3List.addAll(rotatedVec3BasketList);
                List<BlockState> blockStateList=movingBlock.getStateList();
                blockStateList.addAll(movingBlock.getBasketStateList());
                boolean movableFlag=false;
                if (entity.getDeltaMovement().x != 0D) {
                    vx += entity.getDeltaMovement().x * 10D;
                }
                if (entity.getDeltaMovement().z != 0D) {
                    vz += entity.getDeltaMovement().z * 10D;
                }
                Vec3 entityVec3=vec3.add(vx,vy,vz);
                for(int i=0;i<rotatedVec3List.size();i++) {
                    Vec3 eachVec3=rotatedVec3List.get(i);
                    BlockState eachState=blockStateList.get(i);
                    float f1= 0.75F;
                    if(Mth.abs((float) eachVec3.y-(float) entityVec3.y)<f1){
                        if(Mth.abs((float) eachVec3.x-(float) entityVec3.x)<f1){
                            if(Mth.abs((float) eachVec3.z-(float) entityVec3.z)<f1){
                                movableFlag=true;
                            }
                        }
                    }
                }
                if(movableFlag) {
                    moveFunction.accept(entity, vec3.x + vx, vec3.y + vy, vec3.z + vz);
                    setOffset((float) vx, (float) 1D, (float) vz);
                }else{

                    moveFunction.accept(entity, vec3.x + vx0, vec3.y + vy0, vec3.z + vz0);
                }
            }
        }else{
            super.positionRider(entity,moveFunction);
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }
}
