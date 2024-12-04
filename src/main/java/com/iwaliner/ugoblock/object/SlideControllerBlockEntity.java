package com.iwaliner.ugoblock.object;

import com.iwaliner.ugoblock.register.Register;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SlideControllerBlockEntity extends BaseContainerBlockEntity {
  /*  private List<BlockPos> positionList;
    private BlockPos endPos;*/
    private boolean isNotFirstTime;
    private boolean isMoving;
   // private int moveTick;
    private int tickCount;
    private int startTime;
    private int duration=20;
    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
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
    protected final ContainerData durationDataAccess = new ContainerData() {
        public int get(int i) {
            switch(i) {
                case 0:
                    return SlideControllerBlockEntity.this.getDuration();

                default:
                    return 0;
            }
        }

        public void set(int i, int j) {
            switch(i) {
                case 0:
                    SlideControllerBlockEntity.this.duration = j;
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
        return new SlideControllerMenu(i,inventory,this,startTickDataAccess,durationDataAccess);
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

    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
     //   this.endPos=null;
        this.clearPositionList();
        this.isNotFirstTime=false;
        return ContainerHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
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
              //  this.clearPositionList();
                this.setPositionList(ShapeCardItem.getPositionList(stack.getTag()));
            }else if(slot==1&&stack.getItem()==Register.end_location_card.get()&&stack.getTag()!=null){
            //    this.endPos=null;
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
     //   this.positionList = list;
      /*  if(tag.contains("endPos")) {
            this.endPos = NbtUtils.readBlockPos(tag.getCompound("endPos"));
        }*/
        this.isNotFirstTime=tag.getBoolean("isNotFirstTime");
        this.isMoving=tag.getBoolean("isMoving");
      //  this.moveTick=tag.getInt("moveTick");
        this.tickCount=tag.getInt("tickCount");
        this.startTime=tag.getInt("startTime");
        if(tag.contains("duration")) {
            this.duration = tag.getInt("duration");
        }else{
            this.duration = 20;
        }
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);

    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
       /* if(endPos!=null) {
            tag.put("endPos", NbtUtils.writeBlockPos(endPos));
        }*/
        tag.putBoolean("isNotFirstTime",isNotFirstTime);
        tag.putBoolean("isMoving",isMoving);
       // tag.putInt("moveTick",moveTick);
        tag.putInt("tickCount",tickCount);
        tag.putInt("startTime",startTime);
        tag.putInt("duration",duration);

        /*if(positionList!=null) {
            CompoundTag posTag = new CompoundTag();
            for (int i = 0; i < positionList.size(); i++) {
                posTag.put("location_" + String.valueOf(i), NbtUtils.writeBlockPos(positionList.get(i)));
            }
            tag.put("positionList", posTag);
        }*/
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

    public boolean isMoving(){
        return isMoving;
    }
    public void setMoving(boolean b){
        this.isMoving=b;
    }
    public int getMoveTick(){
       // return moveTick;
        return startTime+duration;
    }
    /*public void setMoveTick(int i){
        this.moveTick=i;
    }*/
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
            return EndLocationCardItem.getEndPos(getItem(0).getTag());
        }
      //  return endPos;
        return ShapeCardItem.errorPos();
    }

    public void setEndPos(BlockPos endPos) {
        if(getItem(1).getItem()==Register.end_location_card.get()&&getItem(1).getTag()!=null){
             EndLocationCardItem.setEndPos(getItem(0),endPos);
        }
    //    this.endPos = endPos;
    }

    public List<BlockPos> getPositionList() {
        if(getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null){
            return ShapeCardItem.getPositionList(getItem(0).getTag());
        }
   //     return positionList;
        return new ArrayList<>();
    }
    public void clearPositionList(){
        getItem(0).setTag(null);
    //    positionList.clear();
    }
    public boolean isNotFirstTime(){
        return isNotFirstTime;
    }
    public void setNotFirstTime(boolean b){
        isNotFirstTime=b;
    }

    public void setPositionList(List<BlockPos> positionList) {
        if(getItem(0).getItem()==Register.shape_card.get()&&getItem(0).getTag()!=null){
            ShapeCardItem.setPositionList(getItem(0),positionList);
        }
    //    this.positionList = positionList;
    }
    public void addPositionToList(BlockPos pos){
     //   positionList.add(pos);
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SlideControllerBlockEntity blockEntity) {
        if(blockEntity.getMoveTick()>0){
            if(blockEntity.isMoving()){
                if(blockEntity.getTickCount()> blockEntity.getMoveTick()){
                    blockEntity.setMoving(false);
                    blockEntity.setTickCount(0);
                }
                blockEntity.increaseTickCount(1);
            }
        }

       level.sendBlockUpdated(pos,state,state, Block.UPDATE_ALL);


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
        if(i==1){
            return stack.getItem()==Register.end_location_card.get();
        }else{
            return stack.getItem()==Register.shape_card.get();
        }
    }
}
