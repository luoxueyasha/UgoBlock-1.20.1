package com.iwaliner.ugoblock.object.basket_maker;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.block_imitation_wand.ImitatableBlockEntity;
import com.iwaliner.ugoblock.object.controller.AbstractControllerBlockEntity;
import com.iwaliner.ugoblock.object.controller.RotationControllerBlock;
import com.iwaliner.ugoblock.object.controller.RotationControllerMenu;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class BasketMakerBlockEntity extends AbstractControllerBlockEntity implements ImitatableBlockEntity {
    private int destroyCoolTime;
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);


    public BasketMakerBlockEntity(BlockPos p_155077_, BlockState p_155078_) {
        super(Register.BasketMakerBlockEntity.get(), p_155077_, p_155078_);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ugoblock.basket_maker");
    }
    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new BasketMakerMenu(i,inventory,this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return  items.get(slot);
    }
    public ItemStack removeItem(int ii, int jj) {
        return ContainerHelper.removeItem(this.items, ii, jj);
    }

    public ItemStack removeItemNoUpdate(int p_58387_) {
        return ContainerHelper.takeItem(this.items, p_58387_);
    }
    public void setItem(int slot, ItemStack stack) {
            ItemStack itemstack = this.items.get(slot);
            boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, stack);
            this.items.set(slot, stack);
            if (stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize());
            }

            if (slot == 0 && !flag) {
                this.setChanged();
            }
    }

    public boolean stillValid(Player p_70300_1_) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return p_70300_1_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        destroyCoolTime=tag.getInt("destroyCoolTime");

    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putInt("destroyCoolTime",destroyCoolTime);
    }




    public boolean hasCards(){
        return getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null&&getItem(0).getTag().contains("positionList");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BasketMakerBlockEntity blockEntity) {
        if(state.getBlock() instanceof BasketMakerBlock basketMakerBlock) {
           if(blockEntity.getDestroyCoolTime()==1){
               BasketMakerBlockEntity.breakBlocks(level,blockEntity);
               blockEntity.setDestroyCoolTime(0);
           }else {
               blockEntity.decreaseDestroyCoolTime(1);
           }
        }

    }
    public boolean isWaitingForBreakBlocks(){
        return getDestroyCoolTime()!=0;
    }

    public int getDestroyCoolTime() {
        return destroyCoolTime;
    }

    public void setDestroyCoolTime(int destroyCoolTime) {
        this.destroyCoolTime = destroyCoolTime;
    }
    public void decreaseDestroyCoolTime(int i){
        if(destroyCoolTime-i>0){
            destroyCoolTime=destroyCoolTime-i;
        }else{
            destroyCoolTime=0;
        }
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack stack) {
            return stack.getItem()==Register.shape_card.get();
    }

    public static void breakBlocks(Level level0,BasketMakerBlockEntity blockEntity){
        if(blockEntity!=null&&blockEntity.hasCards()) {
            BlockPos makerPos=blockEntity.getBlockPos();
            List<BlockPos> posList= blockEntity.getPositionList();
            if(posList!=null&&level0!=null) {
                for (int i=0;i<posList.size();i++) {
                    BlockPos eachPos=posList.get(i);
                    BlockState eachState=level0.getBlockState(eachPos);
                    if(!blockEntity.getBlockPos().equals(eachPos)) {
                        if(!eachState.is(Register.TAG_DISABLE_MOVING)) {
                            level0.removeBlockEntity(eachPos);
                            level0.setBlock(eachPos, Blocks.AIR.defaultBlockState(), 82);
                        }
                    }
                }
            }
        }
    }
    public void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
    public void rotateBasketPosList(BlockPos basketMakerPos,BlockState basketMakerState,int degreeAngle,BlockPos rotationControllerOriginPos){
        BlockPos originPos=basketMakerPos.relative(basketMakerState.getValue(BasketMakerBlock.FACING));
        List<BlockPos> posList=getPositionList();
        List<BlockPos> originPosList=getPositionList();
        for(int i=0;i<originPosList.size();i++) {
           originPosList.set(i,originPos);
        }
        List<BlockPos> rotatedBasketPosList = Utils.rotateBasketPosList(posList,rotationControllerOriginPos,rotationControllerOriginPos,basketMakerState.getValue(BasketMakerBlock.FACING).getAxis(),degreeAngle,originPosList);
        Utils.setPositionList(getItem(0),rotatedBasketPosList);
    }
    public boolean hasShapeCard(){
        return !getItem(0).isEmpty()&&getItem(0).is(Register.shape_card.get());
    }
    public boolean hasVectorCard(){
        return true;
    }
}
