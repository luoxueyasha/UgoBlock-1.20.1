package com.iwaliner.ugoblock.object.controllable_block;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ControllableEntity/* extends LivingEntity*/ {
    /*private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(ControllableEntity.class, EntityDataSerializers.BLOCK_STATE);

    public ControllableEntity(EntityType<?> type, Level level) {
        super(Register.ControllableEntity.get(), level);
    }
    public ControllableEntity(Level level, double x, double y, double z, BlockState state) {
        super(Register.ControllableEntity.get(), level);
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
        super.defineSynchedData();
        this.entityData.define(DATA_BLOCK_STATE_ID, Blocks.GOLD_BLOCK.defaultBlockState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("block_state")));

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {

    }

    @Override
    protected boolean canRide(Entity p_20339_) {
        return true;
    }

    *//**trueにすると、当たり判定内に入ったときにはじき出される*//*
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("block_state", NbtUtils.writeBlockState(this.getBlockState()));
    }

    @Override
    public void tick() {
        Level level=level();
        BlockPos pos=blockPosition();
        BlockState state=entityData.get(DATA_BLOCK_STATE_ID);
        if(getFirstPassenger() instanceof Player player){
           Vec3 playerSpeeds= player.getDeltaMovement();
           Vec3 entitySpeeds=this.getDeltaMovement();
           int i0=player.getMainHandItem().getCount();
           this.setDeltaMovement(new Vec3(0.1*i0,entitySpeeds.y,1D));
        }
    }

    @Override
    public boolean isVehicle() {
        return true;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
    @Nullable
    public LivingEntity getControllingPassenger() {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player) {
                Player player = (Player)entity;
               // if (player.getMainHandItem().is(Items.CARROT_ON_A_STICK) || player.getOffhandItem().is(Items.CARROT_ON_A_STICK)) {
                    return player;
             //   }
            }
        return null;
    }
    public boolean isPickable() {
        return true;
    }
    @Override
    public boolean canRiderInteract() {
        return true;
    }
    *//**右クリック時の処理*//*
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
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, (double)0.23F).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }
    public Vec3 getDismountLocationForPassenger(LivingEntity p_29487_) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(p_29487_);
        } else {
            int[][] aint = DismountHelper.offsetsForDirection(direction);
            BlockPos blockpos = this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(Pose pose : p_29487_.getDismountPoses()) {
                AABB aabb = p_29487_.getLocalBoundsForPose(pose);

                for(int[] aint1 : aint) {
                    blockpos$mutableblockpos.set(blockpos.getX() + aint1[0], blockpos.getY(), blockpos.getZ() + aint1[1]);
                    double d0 = this.level().getBlockFloorHeight(blockpos$mutableblockpos);
                    if (DismountHelper.isBlockFloorValid(d0)) {
                        Vec3 vec3 = Vec3.upFromBottomCenterOf(blockpos$mutableblockpos, d0);
                        if (DismountHelper.canDismountTo(this.level(), p_29487_, aabb.move(vec3))) {
                            p_29487_.setPose(pose);
                            return vec3;
                        }
                    }
                }
            }

            return super.getDismountLocationForPassenger(p_29487_);
        }
    }
    protected void tickRidden(Player p_278330_, Vec3 p_278267_) {
        super.tickRidden(p_278330_, p_278267_);
        this.setRot(p_278330_.getYRot(), p_278330_.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
     //  this.steering.tickBoost();
    }

    protected Vec3 getRiddenInput(Player p_278309_, Vec3 p_275479_) {
        return new Vec3(0.0D, 0.0D, 1.0D);
    }

    protected float getRiddenSpeed(Player p_278258_) {
     return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225D*//* * (double)this.steering.boostFactor()*//*);
    }
*/
}
