package com.iwaliner.ugoblock.object.rotation_controller;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerBlock;
import com.iwaliner.ugoblock.object.slide_controller.SlideControllerMenu;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

public class RotationControllerBlockEntity extends BaseContainerBlockEntity {
    private boolean isNotFirstTime;
    private boolean isMoving;
    private int tickCount;
    private int startTime;
    private int degreeAngle=-90;
    private int speedx10=10;
    protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    /**ContainerDataを1つにまとめるとGUI内のボタンを推した時に連動しちゃったから分けてる*/
    protected final ContainerData degreeAngleDataAccess = new ContainerData() {
        public int get(int i) {
            if (i == 0) {
                return RotationControllerBlockEntity.this.getDegreeAngle();
            }
            return 0;
        }

        public void set(int i, int j) {
            if (i == 0) {
                RotationControllerBlockEntity.this.degreeAngle = j;
            }

        }

        public int getCount() {
            return 1;
        }
    };

    protected final ContainerData speedDataAccess = new ContainerData() {
        public int get(int i) {
            if (i == 0) {
                return getSpeedx10();
            }
            return 0;
        }

        public void set(int i, int j) {
            if (i == 0) {
                RotationControllerBlockEntity.this.speedx10 = j;
            }

        }

        public int getCount() {
            return 1;
        }
    };
    public RotationControllerBlockEntity(BlockPos p_155077_, BlockState p_155078_) {
        super(Register.RotationController.get(), p_155077_, p_155078_);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ugoblock.rotation_controller");
    }
    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new RotationControllerMenu(i,inventory,this,degreeAngleDataAccess,speedDataAccess);
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
            if(slot==0&&stack.getItem()==Register.shape_card.get()){
                this.setPositionList(Utils.getPositionList(stack.getTag()));
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
        List<BlockPos> list=new ArrayList<>();
        CompoundTag posTag=tag.getCompound("positionList");
        for(String s : posTag.getAllKeys()){
                list.add(NbtUtils.readBlockPos(posTag.getCompound(s)));
            }
        this.isNotFirstTime=tag.getBoolean("isNotFirstTime");
        this.isMoving=tag.getBoolean("isMoving");
        this.tickCount=tag.getInt("tickCount");
        this.startTime=tag.getInt("startTime");
        if(tag.contains("speedx10")) {
            this.speedx10 = tag.getInt("speedx10");
        }else{
            this.speedx10 = 10;
        }
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);

    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isNotFirstTime",isNotFirstTime);
        tag.putBoolean("isMoving",isMoving);
       tag.putInt("tickCount",tickCount);
        tag.putInt("startTime",startTime);
       tag.putInt("speedx10",speedx10);
        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag= super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }
    public BlockPos getStartPos(){
        if(level.getBlockState(getBlockPos()).getBlock()instanceof RotationControllerBlock){
            return getBlockPos().relative(level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING));
        }
        return getBlockPos();
    }

    public boolean isMoving(){
        return isMoving;
    }
    public void setMoving(boolean b){
        this.isMoving=b;
    }
    public int getMoveTick(){
        return startTime+getDuration();
    }
    public int getTickCount(){
        return tickCount;
    }
    public void setTickCount(int i){
        this.tickCount=i;
    }
    public void increaseTickCount(int i){
        this.tickCount+=i;
    }




    public List<BlockPos> getPositionList() {
        if(getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null){
            return Utils.getPositionList(getItem(0).getTag());
        }
       return new ArrayList<>();
    }
    public void clearPositionList(){
        getItem(0).setTag(null);
    }
    public boolean isNotFirstTime(){
        return isNotFirstTime;
    }
    public void setNotFirstTime(boolean b){
        isNotFirstTime=b;
    }

    public void setPositionList(List<BlockPos> positionList) {
        if(getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null){
            Utils.setPositionList(getItem(0),positionList);
        }
    }


    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    public int getDegreeAngle(){
        return degreeAngle;
    }
    public void setDegreeAngle(int degree){
        degreeAngle=degree;
    }
    public int getSpeedx10(){
        return  speedx10;
    }
    public void setSpeedx10(int speed){
        speedx10=speed;
    }

    public int getDuration() {
        double distance=Mth.PI*((float) getDegreeAngle()/180f);
        double d=(distance/((double) speedx10/10D))*20D;
        return Math.round((float) d);
    }


    public boolean hasCards(){
        return getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null&&getItem(0).getTag().contains("positionList");
    }
    private boolean isCounterClockwise(){
        return getBlockState().getBlock() instanceof RotationControllerBlock&&getBlockState().getValue(RotationControllerBlock.COUNTER_CLOCKWISE);
    }
    private void setTurnDirection(boolean isCounterClockwise) {
        if (getBlockState().getBlock() instanceof RotationControllerBlock) {
            level.setBlock(getBlockPos(), getBlockState().setValue(RotationControllerBlock.COUNTER_CLOCKWISE, isCounterClockwise), 2);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RotationControllerBlockEntity blockEntity) {
        if(state.getBlock() instanceof RotationControllerBlock) {
            if(blockEntity.isMoving()&&!state.getValue(RotationControllerBlock.MOVING)){
                level.setBlock(pos,state.setValue(RotationControllerBlock.MOVING,true),2);
            }else if(!blockEntity.isMoving()&&state.getValue(RotationControllerBlock.MOVING)){
                level.setBlock(pos,state.setValue(RotationControllerBlock.MOVING,false),2);
            }
            if (blockEntity.getMoveTick() > 0) {
                if (blockEntity.isMoving()) {
                    if (blockEntity.getTickCount() > blockEntity.getMoveTick()) {
                        blockEntity.setMoving(false);
                        blockEntity.setTickCount(0);
                    } else if (blockEntity.getTickCount() == blockEntity.getMoveTick() && blockEntity.hasCards()) {
                        BlockPos startPos = blockEntity.getStartPos();
                     //   List<BlockPos> posList = blockEntity.getPositionList();
                        if (blockEntity.isNotFirstTime()) {
                            List<BlockPos> posList0 = blockEntity.getPositionList();
                            for (int i = 0; i < posList0.size(); i++) {
                             }
                        }
                     //   blockEntity.setPositionList(posList);
                    }
                    blockEntity.increaseTickCount(1);
                }

            }

            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }

       }
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack stack) {
            return stack.getItem()==Register.shape_card.get();
    }

}
