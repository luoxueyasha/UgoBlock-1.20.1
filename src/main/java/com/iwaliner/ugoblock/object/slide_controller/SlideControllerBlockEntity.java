package com.iwaliner.ugoblock.object.slide_controller;

import com.iwaliner.ugoblock.Utils;
import com.iwaliner.ugoblock.object.EndLocationCardItem;
import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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

public class SlideControllerBlockEntity extends BaseContainerBlockEntity {
    private boolean isNotFirstTime;
    private boolean isMoving;
    private int tickCount;
    private int startTime;
    private int speedx10=10;
    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    /**ContainerDataを1つにまとめるとGUI内のボタンを推した時に連動しちゃったから分けてる*/
    protected final ContainerData startTickDataAccess = new ContainerData() {
        public int get(int i) {
            switch(i) {
                case 0:
                    return SlideControllerBlockEntity.this.getStartTime();

                default:
                    return 0;
            }
        }

        public void set(int i, int j) {
            switch(i) {
                case 0:
                    SlideControllerBlockEntity.this.startTime = j;
            }

        }

        public int getCount() {
            return 1;
        }
    };
    protected final ContainerData speedDataAccess = new ContainerData() {
        public int get(int i) {
            switch(i) {
                case 0:
                    return getSpeedx10();

                default:
                    return 0;
            }
        }

        public void set(int i, int j) {
            switch(i) {
                case 0:
                    SlideControllerBlockEntity.this.speedx10=j;
            }

        }

        public int getCount() {
            return 1;
        }
    };
    public SlideControllerBlockEntity( BlockPos p_155077_, BlockState p_155078_) {
        super(Register.SlideController.get(), p_155077_, p_155078_);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ugoblock.slide_controller");
    }
    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new SlideControllerMenu(i,inventory,this,startTickDataAccess,speedDataAccess);
    }

    @Override
    public int getContainerSize() {
        return 2;
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
            }else if(slot==1&&stack.getItem()==Register.end_location_card.get()&&stack.getTag()!=null){
                 this.setEndPos(EndLocationCardItem.getEndPos(stack.getTag()));
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
        //tag.putInt("duration",duration);
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
        if(level.getBlockState(getBlockPos()).getBlock()instanceof SlideControllerBlock){
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


    public BlockPos getEndPos() {
        if(getItem(1).getItem()==Register.end_location_card.get()&&getItem(1).getTag()!=null){
            return EndLocationCardItem.getEndPos(getItem(1).getTag());
        }
       return Utils.errorPos();
    }

    public void setEndPos(BlockPos endPos) {
        if(getItem(1).getItem()==Register.end_location_card.get()&&getItem(1).getTag()!=null){
             EndLocationCardItem.setEndPos(getItem(1),endPos);
        }
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
    public int getSpeedx10(){
        return  speedx10;
    }
    public void setSpeedx10(int speed){
        speedx10=speed;
    }

    public int getDuration() {
        double distance=this.getDistance();
        double d=(distance/((double) speedx10/10D))*20D;
        return Math.round((float) d);
    }
    public double getDistance(){
        BlockPos startPos=this.getStartPos();
        BlockPos endPos=this.getEndPos();
        return Mth.sqrt((float)Mth.square(startPos.getX()-endPos.getX())+(float)Mth.square(startPos.getY()-endPos.getY())+(float)Mth.square(startPos.getZ()-endPos.getZ()));
    }

    public boolean hasCards(){
        return getItem(0).getItem()==Register.shape_card.get()&&getItem(1).getItem()==Register.end_location_card.get()&&getItem(0).getTag()!=null&&getItem(1).getTag()!=null&&getItem(0).getTag().contains("positionList")&&getItem(1).getTag().contains("end_location");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SlideControllerBlockEntity blockEntity) {
        if(state.getBlock() instanceof SlideControllerBlock) {
            if(blockEntity.isMoving()&&!state.getValue(SlideControllerBlock.MOVING)){
                level.setBlock(pos,state.setValue(SlideControllerBlock.MOVING,true),2);
            }else if(!blockEntity.isMoving()&&state.getValue(SlideControllerBlock.MOVING)){
                level.setBlock(pos,state.setValue(SlideControllerBlock.MOVING,false),2);
            }
            if (blockEntity.getMoveTick() > 0) {
                if (blockEntity.isMoving()) {
                    if (blockEntity.getTickCount() > blockEntity.getMoveTick()) {
                        blockEntity.setMoving(false);
                        blockEntity.setTickCount(0);
                    } else if (blockEntity.getTickCount() == blockEntity.getMoveTick() && blockEntity.hasCards()) {
                        BlockPos startPos = blockEntity.getStartPos();
                        List<BlockPos> posList = blockEntity.getPositionList();
                        BlockPos endPos = new BlockPos(startPos.getX() + (startPos.getX() - blockEntity.getEndPos().getX()), startPos.getY() + (startPos.getY() - blockEntity.getEndPos().getY()), startPos.getZ() + (startPos.getZ() - blockEntity.getEndPos().getZ()));
                        if (blockEntity.isNotFirstTime()) {
                            List<BlockPos> posList0 = blockEntity.getPositionList();
                            for (int i = 0; i < posList0.size(); i++) {
                                posList.set(i, new BlockPos(posList0.get(i).getX() + (startPos.getX() - blockEntity.getEndPos().getX()), posList0.get(i).getY() + (startPos.getY() - blockEntity.getEndPos().getY()), posList0.get(i).getZ() + (startPos.getZ() - blockEntity.getEndPos().getZ())));
                            }
                        }
                        blockEntity.setPositionList(posList);
                        blockEntity.setEndPos(endPos);
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
    public boolean canPlaceItem(int i, ItemStack stack) {
        if(i==1){
            return stack.getItem()==Register.end_location_card.get();
        }else{
            return stack.getItem()==Register.shape_card.get();
        }
    }



}
