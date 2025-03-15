package com.iwaliner.ugoblock.object.gravitate_block;

import com.iwaliner.ugoblock.ModCoreUgoBlock;
import com.iwaliner.ugoblock.mixin.FallingBlockMixin;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GravitateBlockEntity extends FallingBlockEntity {
    private boolean cancelDrop=false;

    public GravitateBlockEntity(EntityType<? extends GravitateBlockEntity> p_i48580_1_, Level p_i48580_2_) {
        super(Register.GravitateBlock.get(), p_i48580_2_);
    }
    public GravitateBlockEntity(Level p_i1705_1_, double p_i1705_2_, double p_i1705_4_, double p_i1705_6_, BlockState state) {
        this(Register.GravitateBlock.get(), p_i1705_1_);
        this.setPos(p_i1705_2_, p_i1705_4_, p_i1705_6_);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = p_i1705_2_;
        this.yo = p_i1705_4_;
        this.zo = p_i1705_6_;
        ((FallingBlockMixin) this).setBlockState(state);
    }
    public GravitateBlockEntity(Level p_i1705_1_, BlockPos pos, BlockState state) {
        this(Register.GravitateBlock.get(), p_i1705_1_);
        this.setPos(pos.getCenter().add(0D,-0.5D,0D));
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = pos.getCenter().x;
        this.yo = pos.getCenter().y-0.5D;
        this.zo = pos.getCenter().z;
        ((FallingBlockMixin) this).setBlockState(state);
    }
    private boolean shouldMakeBlock(){
        Vec3 vec3=this.getDeltaMovement();
        return Mth.abs((float) vec3.x)<0.01D&&Mth.abs((float) vec3.y)<0.01D&&Mth.abs((float) vec3.z)<0.01D;
    }
    public boolean isPickable() {
        return true;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    /*@Override
    public boolean canCollideWith(Entity p_20303_) {
        return true;
    }*/

    @Override
    protected boolean canRide(Entity p_20339_) {
        return true;
    }
    @Override
    public boolean canRiderInteract() {
        return true;
    }

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

    public void tick() {
        double offsetY=getOffsetY();
        if (this.getBlockState().isAir()) {
            this.discard();
        } else {
            if(!this.level().isClientSide()&&this.getPassengers().isEmpty()) {
                for (Entity entity : level().getEntities((Entity) null, new AABB(blockPosition()).inflate(0d, 0.1d, 0d), (o) -> {
                    return o instanceof LivingEntity;
                })) {
                    if(!entity.isPassenger()) {
                        entity.startRiding(this);
                        level().playSound((Player) null, this.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        break;
                    }
                }
            }
            Block block = this.getBlockState().getBlock();
            ++this.time;
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
            if (!this.level().isClientSide) {
                BlockPos blockpos = this.blockPosition();

                boolean flag = this.getBlockState().getBlock() instanceof ConcretePowderBlock;
                boolean flag1 = flag && this.getBlockState().canBeHydrated(this.level(), blockpos, this.level().getFluidState(blockpos), blockpos);
                double d0 = this.getDeltaMovement().lengthSqr();
                if (flag && d0 > 1.0D) {
                    BlockHitResult blockhitresult = this.level().clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
                    if (blockhitresult.getType() != HitResult.Type.MISS && this.getBlockState().canBeHydrated(this.level(), blockpos, this.level().getFluidState(blockhitresult.getBlockPos()), blockhitresult.getBlockPos())) {
                        blockpos = blockhitresult.getBlockPos();
                        flag1 = true;
                    }
                }

                if (!this.onGround() && !flag1) {
                    if(getFirstPassenger()!=null) {
                        //this.stopRiding();
                        getFirstPassenger().setPos(position().add(0D, offsetY, 0D));
                    }
                    if (!this.level().isClientSide && (this.time > 100 && (blockpos.getY() <= this.level().getMinBuildHeight() || blockpos.getY() > this.level().getMaxBuildHeight()) || this.time > 600)) {
                        if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(block);
                        }

                        this.discard();
                    }
                } else if(shouldMakeBlock()){
                    if(getFirstPassenger()!=null) {
                        //this.stopRiding();
                        getFirstPassenger().setPos(position().add(0D, offsetY, 0D));
                    }
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
                    if (!blockstate.is(Blocks.MOVING_PISTON)) {
                        if (!this.cancelDrop) {
                            boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level(), blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                            boolean flag3 = FallingBlock.isFree(this.level().getBlockState(blockpos.below())) && (!flag || !flag1);
                            boolean flag4 = this.getBlockState().canSurvive(this.level(), blockpos) && !flag3;
                            if (flag2 && flag4) {
                                if (this.getBlockState().hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(blockpos).getType() == Fluids.WATER) {
                                   // this.blockState = this.getBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                                    ((FallingBlockMixin) this).setBlockState(this.getBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)));
                                }
                                if(getFirstPassenger()!=null) {
                                   // this.stopRiding();
                                    getFirstPassenger().setPos(position().add(0D, offsetY, 0D));
                                }

                                if (this.level().setBlock(blockpos, this.getBlockState(), 3)) {
                                    ((ServerLevel)this.level()).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level().getBlockState(blockpos)));
                                    this.discard();
                                    if (block instanceof Fallable) {
                                        ((Fallable)block).onLand(this.level(), blockpos, this.getBlockState(), blockstate, this);
                                    }

                                    if (this.blockData != null && this.getBlockState().hasBlockEntity()) {
                                        BlockEntity blockentity = this.level().getBlockEntity(blockpos);
                                        if (blockentity != null) {
                                            CompoundTag compoundtag = blockentity.saveWithoutMetadata();

                                            for(String s : this.blockData.getAllKeys()) {
                                                compoundtag.put(s, this.blockData.get(s).copy());
                                            }

                                            try {
                                                blockentity.load(compoundtag);
                                            } catch (Exception exception) {
                                                ModCoreUgoBlock.logger.error("Failed to load block entity from gravitite block", (Throwable)exception);
                                            }

                                            blockentity.setChanged();
                                        }
                                    }
                                }else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    if(getFirstPassenger()!=null) {
                                        //this.stopRiding();
                                        getFirstPassenger().setPos(position().add(0D, offsetY, 0D));
                                    }
                                    this.discard();
                                    this.callOnBrokenAfterFall(block, blockpos);
                                    this.spawnAtLocation(block);
                                }
                            } else {
                                if(getFirstPassenger()!=null) {
                                    //this.stopRiding();
                                    getFirstPassenger().setPos(position().add(0D, offsetY, 0D));
                                }
                                this.discard();
                                if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                                    this.callOnBrokenAfterFall(block, blockpos);
                                    this.spawnAtLocation(block);
                                }
                            }
                        } else {
                            if(getFirstPassenger()!=null) {
                                //this.stopRiding();
                                getFirstPassenger().setPos(position().add(0D, offsetY, 0D));
                            }
                            this.discard();
                            this.callOnBrokenAfterFall(block, blockpos);
                        }
                    }
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        }
    }
    private double getOffsetY(){
        return 1.01D;
    }
    public boolean hurt(DamageSource damageSource, float p_70097_2_) {
        Block block = this.getBlockState().getBlock();
        BlockPos blockpos = this.blockPosition();
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level().isClientSide) {
                if(getFirstPassenger()!=null) {
                    getFirstPassenger().setPos(position().add(0D, getOffsetY(), 0D));
                }
                this.discard();
                this.callOnBrokenAfterFall(block, blockpos);
                this.spawnAtLocation(block);
                this.markHurt();
                this.playSound(SoundEvents.WOOL_BREAK, 1.0F, 1.0F);
            }

            return true;
        }
    }

}
