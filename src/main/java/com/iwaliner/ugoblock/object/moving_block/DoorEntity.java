package com.iwaliner.ugoblock.object.moving_block;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

public class DoorEntity extends Entity {

    public DoorEntity(EntityType<?> p_270360_, Level p_270280_) {
        super(Register.DoorEntity.get(), p_270280_);
    }
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    public DoorEntity(Level level, BlockPos pos) {
        super(Register.DoorEntity.get(), level);
        this.setPos(pos.getX()+0.5D, pos.getY(), pos.getZ()+0.5D);
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
    protected void addAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();
    }


    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
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
        return false;
    }
    /**右クリック時の処理*/
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(this.isPassenger()&&this.getVehicle() instanceof MovingBlockEntity movingBlock){
            SoundEvent soundEvent=null;
           for(int i=0;i<movingBlock.getPassengers().size();i++){
               Entity passenger=movingBlock.getPassengers().get(i);
               if(passenger.getUUID()==this.getUUID()){
                   BlockPos seatPos=movingBlock.getSeatPosList().get(i);
                   for(int j=0;j<movingBlock.getPosList().size();j++){
                       BlockPos eachPos=movingBlock.getPosList().get(j);
                       if(seatPos.equals(eachPos)){
                           BlockState state=movingBlock.getStateList().get(j);
                         soundEvent=  changeState(level(),player,seatPos,eachPos,state,movingBlock,j,false);
                       }
                   }
                   for(int j=0;j<movingBlock.getBasketPosList().size();j++){
                       BlockPos eachPos=movingBlock.getBasketPosList().get(j);
                       if(seatPos.equals(eachPos)){
                            BlockState state=movingBlock.getBasketStateList().get(j);
                          soundEvent= changeState(level(),player,seatPos,eachPos,state,movingBlock,j,true);
                       }
                   }
               }
           }
           if(soundEvent!=null){
               player.level().playSound(player,player.blockPosition(), soundEvent,SoundSource.BLOCKS,1f,1f);
           }
           return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
    private SoundEvent changeState(Level level, Player player, BlockPos pos, BlockPos eachPos, BlockState state, MovingBlockEntity movingBlock, int i, boolean isBasket){
            if (state.getBlock() instanceof DoorBlock doorBlock && doorBlock.type().canOpenByHand()) {
                setState(state.cycle(DoorBlock.OPEN), movingBlock, i, isBasket);
                if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                    int underIndex = getPosList(movingBlock, isBasket).indexOf(eachPos.below());
                    setState(state.cycle(DoorBlock.OPEN).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), movingBlock, underIndex, isBasket);
                    return state.getValue(DoorBlock.OPEN)?doorBlock.type().doorClose() : doorBlock.type().doorOpen();
                } else {
                    int upperIndex = getPosList(movingBlock, isBasket).indexOf(eachPos.above());
                    setState(state.cycle(DoorBlock.OPEN).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), movingBlock, upperIndex, isBasket);
                    return state.getValue(DoorBlock.OPEN)?doorBlock.type().doorClose() : doorBlock.type().doorOpen();
                }
            } else if (state.getBlock() instanceof FenceGateBlock fenceGateBlock) {
                setState(state.cycle(FenceGateBlock.OPEN), movingBlock, i, isBasket);
                return state.getValue(FenceGateBlock.OPEN)? SoundEvents.FENCE_GATE_CLOSE : SoundEvents.FENCE_GATE_OPEN;
            }
            return null;
    }
    private List<BlockPos> getPosList(MovingBlockEntity movingBlock,boolean isBasket){
        return isBasket? movingBlock.getBasketPosList() : movingBlock.getPosList();
    }
    private  void setState(BlockState state,MovingBlockEntity movingBlock,int i,boolean isBasket){
        if(isBasket){
            movingBlock.setBasketState(i,state);
        }else{
            movingBlock.setState(i,state);
        }
    }

    @Override
    public boolean hurt(DamageSource p_19946_, float p_19947_) {
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
}
