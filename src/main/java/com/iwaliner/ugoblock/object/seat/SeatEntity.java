package com.iwaliner.ugoblock.object.seat;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.object.moving_block.MovingBlockEntity;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class SeatEntity extends Entity {
    public static final EntityDataAccessor<Boolean> DATA_ROTATING_ID = SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);

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
        double bigger=0.3D;
        AABB axisalignedbb =this.getBoundingBox()/*.deflate(bigger, bigger, bigger)*/;
        List<LivingEntity> list =level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        if(!level().isClientSide()&&this.getPassengers().isEmpty()){
            if(!list.isEmpty()) {
                for (LivingEntity entity : list) {
                    if (entity instanceof Player) {
                    } else {
                    if(!entity.isPassenger()) {
                        entity.startRiding(this);
                        level().playSound((Player) null, this.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        return;
                              }
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
       // int i = this.getPosRotInterpolationDuration();
        this.lerpX = p_297677_;
        this.lerpY = p_301293_;
        this.lerpZ = p_301384_;
        this.lerpYRot = (double)p_300635_;
        this.lerpXRot = (double)p_299108_;
        this.lerpSteps = 10;
        //this.posRotInterpolationTarget = new MovingBlockEntity.PosRotInterpolationTarget(i, p_297677_, p_301293_, p_301384_, (double)p_300635_, (double)p_299108_);
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
            double d3 = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
            //this.setYRot(this.getYRot() + (float)d3 / (float)this.lerpSteps);
            //this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            // this.setYRot(this.getYRot() % 360.0F);

        }
        if (this.isControlledByLocalInstance() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.absMoveTo(this.lerpX, this.lerpY, this.lerpZ, (float)this.lerpYRot, (float)this.lerpXRot);
        }
    }
}
